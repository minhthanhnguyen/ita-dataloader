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
      <md-table>
        <md-table-row>
          <md-table-head>File Name</md-table-head>
          <md-table-head>URL</md-table-head>
          <md-table-head>Uploaded At</md-table-head>
          <md-table-head>Uploaded By</md-table-head>
          <md-table-head md-numeric>Size</md-table-head>
        </md-table-row>

        <md-table-row v-for="file in storageContent" v-bind:key="file.name">
          <md-table-cell>{{file.name}}</md-table-cell>
          <md-table-cell>
            <a v-bind:href="file.url">{{file.url}}</a>
          </md-table-cell>
          <md-table-cell>{{file.uploadedAt}}</md-table-cell>
          <md-table-cell>{{file.uploadedBy}}</md-table-cell>
          <md-table-cell md-numeric>{{file.size}}</md-table-cell>
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
      storageContent: [],
      destinationFileNameOptions: [],
      originalFileName: null,
      destinationFileName: null,
      errorOccured: false,
      errorMessages: [],
      uploadSuccessful: false,
      uploading: false,
      fileBlob: null,
      loading: true
    };
  },
  methods: {
    async updateBusinessUnitContent() {
      this.storageContent = await this.dataloaderRepository._getStorageContent(
        this.containerName
      );

      this.destinationFileNameOptions = this.storageContent.map(
        file => file.name
      );

      this.businessUnitName = this.businessUnits.find(
        b => b.containerName === this.containerName
      ).businessName;
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
