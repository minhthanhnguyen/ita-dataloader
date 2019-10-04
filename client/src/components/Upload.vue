<template>
  <div>
    <dataloader-header v-bind:businessUnitName="businessUnitName" />
    <div class="md-layout md-gutter">
      <div class="md-layout-item md-size-10">
        <div class="nav-btns">
          <md-button class="md-icon-button url-config-btn" @click="goToUrlIngestConfig()">
            <md-icon class="fa fa-cog"></md-icon>
          </md-button>
          <md-button class="md-icon-button url-log-btn" @click="goToUrlIngestLog()">
            <md-icon class="fa fa-bars"></md-icon>
          </md-button>
        </div>
      </div>
      <div class="md-layout-item md-size-20">
        <md-field>
          <label>Business Unit</label>
          <md-select v-model="containerName" @md-selected="updateBusinessUnitContent()">
            <md-option
              v-for="business in businessUnits"
              v-bind:key="business.containerName"
              v-bind:value="business.containerName"
            >{{business.businessName}}</md-option>
          </md-select>
        </md-field>
        <span class="file-stat">
          <strong>Total files:</strong>
          {{totalFiles}}
        </span>
        <span class="file-stat">
          <strong>Total from API:</strong>
          {{totalFilesFromAPIs}}
        </span>
      </div>
      <div class="md-layout-item md-size-20">
        <md-field>
          <label>Select file</label>
          <md-file @md-change="onFileSelection($event)"></md-file>
        </md-field>
      </div>
      <div class="md-layout-item md-size-40">
        <md-autocomplete v-model="destinationFileName" :md-options="destinationFileNameOptions">
          <label>File name</label>
        </md-autocomplete>
      </div>
      <div class="md-layout-item md-size-10">
        <md-button
          v-if="!uploading"
          class="md-primary md-raised top-btn"
          @click="uploadFile()"
        >Upload</md-button>
        <div v-if="uploading" class="spinner">
          <md-progress-spinner md-mode="indeterminate" :md-diameter="30"></md-progress-spinner>
        </div>
      </div>
    </div>
    <div v-if="loading" class="loading">loading...</div>
    <div class="user-feedback">
      <div class="error" v-if="errorOccured">
        <ul>
          <li v-for="message in errorMessages" v-bind:key="message">{{message}}</li>
        </ul>
      </div>
      <md-dialog-alert
        :md-active.sync="uploadSuccessful"
        md-content="Your file was uploaded successfully! "
        md-confirm-text="Close"
      />
    </div>
    <div v-if="!loading" class="md-layout md-alignment-top-center storage-content">
      <md-table v-model="storageMetadata" md-sort="name" md-sort-order="asc">
        <md-table-row slot-scope="{ item }" slot="md-table-row">
          <md-table-cell md-label="File Name" md-sort-by="name">{{item.name}}</md-table-cell>
          <md-table-cell md-label="URL">
            <a v-bind:href="item.url">{{item.url}}</a>
          </md-table-cell>
          <md-table-cell md-label="Uploaded At" md-sort-by="uploadedAt">{{item.uploadedAt}}</md-table-cell>
          <md-table-cell md-label="Uploaded By" md-sort-by="uploadedBy">{{item.uploadedBy}}</md-table-cell>
          <md-table-cell md-label="Size" md-sort-by="size" md-numeric>{{item.size}}</md-table-cell>
          <md-table-cell v-if="item.fromAPI === 'true'" md-label="API" md-sort-by="fromAPI">
            <span class="dot api-dot"></span>
          </md-table-cell>
          <md-table-cell v-if="item.fromAPI === 'false'" md-label="API" md-sort-by="fromAPI">
            <span class="dot"></span>
          </md-table-cell>
        </md-table-row>
      </md-table>
    </div>
  </div>
</template>
<style>
.user-feedback {
  display: flex;
  justify-content: center;
}
.error {
  color: red;
}
.success {
  color: green;
}
.uploading {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
.storage-content {
  padding-top: 10px;
}
.file-stat {
  margin-right: 15px;
}
.dot {
  height: 10px;
  width: 10px;
  border-radius: 50%;
  display: inline-block;
  border: 1px solid black;
}

.dot.api-dot {
  background-color: #1b51ab;
  border: 1px solid #1b51ab;
}
</style>
<script>
import { readUploadedFileAsArrayBuffer } from "./FileHelper";
import Header from "./Header";

export default {
  name: "Upload",
  props: {
    dataloaderRepository: Object
  },
  components: {
    "dataloader-header": Header
  },
  async created() {
    this.loading = true;
    this.businessUnits = await this.dataloaderRepository._getBusinessUnits();

    if (this.$route.params["containerName"]) {
      this.containerName = this.$route.params["containerName"];
    } else {
      this.containerName = this.businessUnits[0].containerName;
    }

    await this.updateBusinessUnitContent();
    this.loading = false;
  },
  data() {
    return {
      containerName: null,
      businessUnitName: null,
      businessUnits: [],
      storageMetadata: [],
      destinationFileNameOptions: [],
      originalFileName: null,
      destinationFileName: null,
      errorOccured: false,
      errorMessages: [],
      uploadSuccessful: false,
      uploading: false,
      fileBlob: null,
      loading: true,
      totalFiles: 0,
      totalFilesFromAPIs: 0
    };
  },
  methods: {
    async updateBusinessUnitContent() {
      let storageMetadata = await this.dataloaderRepository._getStorageMetadata(
        this.containerName
      );

      this.totalFiles = storageMetadata.length;

      this.destinationFileNameOptions = storageMetadata.map(file => file.name);

      this.businessUnitName = this.businessUnits.find(
        b => b.containerName === this.containerName
      ).businessName;

      const dataloaderConfig = await this.dataloaderRepository._getDataloaderConfig(
        this.containerName
      );
      let dataSetFileNames = dataloaderConfig.dataSetConfigs.map(
        config => config.fileName
      );
      this.storageMetadata = storageMetadata.map(metadata => {
        metadata.fromAPI = dataSetFileNames.includes(metadata.name).toString();
        return metadata;
      });

      this.totalFilesFromAPIs = this.storageMetadata.filter(
        metadata => metadata.fromAPI === "true"
      ).length;
    },
    onFileSelection(event) {
      this.fileBlob = event[0];
      this.originalFileName = event[0].name;
      this.destinationFileName = event[0].name;
      this.uploadSuccessful = false;
    },
    async uploadFile() {
      this.uploading = true;
      this.uploadSuccessful = false;
      this.errorMessages = [];
      if (!this.fileBlob) {
        this.setErrorState(["Please select a file to be uploaded."]);
        return;
      }

      if (!this.destinationFileName) {
        this.setErrorState([
          "Please select a destination file name for the upload."
        ]);
        return;
      }

      const formData = new FormData();
      formData.append("file", this.fileBlob, this.destinationFileName);
      const message = await this.dataloaderRepository._save(
        this.containerName,
        formData
      );
      this.uploadSuccessful = true;
      this.uploading = false;

      await this.updateBusinessUnitContent();
    },
    setErrorState(errorMessages) {
      this.errorOccured = true;
      this.errorMessages = errorMessages;
      this.uploadSuccessful = false;
      this.uploading = false;
    },
    goToUrlIngestConfig() {
      this.$router.push({
        name: "Config",
        params: {
          containerName: this.containerName
        }
      });
    },
    goToUrlIngestLog() {
      this.$router.push({
        name: "Log",
        params: {
          containerName: this.containerName
        }
      });
    }
  }
};
</script>
