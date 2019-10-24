<template>
  <div>
    <dataloader-header v-bind:businessUnitName="businessUnitName" />
    <div class="content">
      <dataloader-menu v-bind:containerName="containerName" />
      <div class="sub-content">
        <div class="md-layout md-gutter">
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
          <div class="md-layout-item md-size-30">
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
        <div v-if="loading" class="loading">
          <md-progress-bar md-mode="indeterminate"></md-progress-bar>
        </div>
        <div v-if="!loading" class="md-layout md-gutter">
          <div class="md-layout-item md-size-100">
            <span class="stat">
              <strong>TOTAL FILES:</strong>
              {{totalFiles}}
            </span>
            <span class="stat">
              <strong>UPLOADS:</strong>
              {{totalManualUploads}}
            </span>
            <span class="stat">
              <strong>PIPELINE:</strong>
              <span v-if="!['Succeeded', 'InProgress', 'n/a'].includes(this.pipelineStatus.status)">
                <a @click="displayPipelineMessage = true" href="#">Error</a>
              </span>
              <span v-else>{{this.pipelineStatus.status}}</span>
              <span>{{this.pipelineStatus.runEnd}}</span>
            </span>
            <a @click="updateBusinessUnitContent()" href="#">Refresh</a>
          </div>
        </div>
        <div v-if="!loading" class="md-layout storage-content">
          <md-table v-model="storageMetadata" md-sort="name" md-sort-order="asc">
            <md-table-row slot-scope="{ item }" slot="md-table-row">
              <md-table-cell md-label="File Name" md-sort-by="fileName">
                <a v-bind:href="item.url">{{item.fileName}}</a>
              </md-table-cell>
              <md-table-cell md-label="Uploaded At" md-sort-by="uploadedAt">{{item.uploadedAt}}</md-table-cell>
              <md-table-cell
                md-label="Uploaded By"
                md-sort-by="metadata.uploaded_by"
              >{{item.metadata.uploaded_by}}</md-table-cell>
              <md-table-cell md-label="Size" md-sort-by="size" md-numeric>{{item.size}}</md-table-cell>
              <md-table-cell md-label="Upload" md-sort-by="metadata.user_upload">
                <span v-if="item.metadata.user_upload === 'true'" class="dot filled"></span>
                <span v-else class="dot"></span>
              </md-table-cell>
            </md-table-row>
          </md-table>
        </div>
        <div class="user-feedback">
          <md-dialog-alert
            :md-active.sync="uploadSuccessful"
            md-title="Upload Successful!"
            md-content="Your file was uploaded successfully! "
            md-confirm-text="Close"
          />
          <md-dialog-alert
            :md-active.sync="errorOccured"
            md-title="Upload Error!"
            v-bind:md-content="errorMessage"
            md-confirm-text="Close"
          />
          <md-dialog-alert
            :md-active.sync="displayPipelineMessage"
            md-title="Pipeline Error!"
            v-bind:md-content="pipelineStatus.message"
            md-confirm-text="Close"
          />
        </div>
      </div>
    </div>
  </div>
</template>
<style>
.user-feedback {
  display: flex;
  justify-content: center;
}

.uploading {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.storage-content {
  padding-top: 10px;
}

.dot {
  height: 10px;
  width: 10px;
  border-radius: 50%;
  display: inline-block;
  border: 1px solid black;
}
.dot.filled {
  background-color: #1b51ab;
  border: 1px solid #1b51ab;
}
</style>
<script>
import Menu from "./Menu";
import Header from "./Header";
import { readUploadedFileAsArrayBuffer } from "./FileHelper";

export default {
  name: "Upload",
  props: {
    dataloaderRepository: Object
  },
  components: {
    "dataloader-header": Header,
    "dataloader-menu": Menu
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
      errorMessage: null,
      uploadSuccessful: false,
      uploading: false,
      fileBlob: null,
      loading: true,
      totalFiles: 0,
      totalManualUploads: 0,
      pipelineStatus: {
        status: "n/a",
        message: "",
        runEnd: ""
      },
      displayPipelineMessage: false
    };
  },
  methods: {
    async updateBusinessUnitContent() {
      this.loading = true;
      this.storageMetadata = await this.dataloaderRepository._getStorageMetadata(
        this.containerName
      );

      this.totalFiles = this.storageMetadata.length;

      this.businessUnitName = this.businessUnits.find(
        b => b.containerName === this.containerName
      ).businessName;

      this.destinationFileNameOptions = this.storageMetadata
        .filter(file => file.metadata.user_upload === "true")
        .map(file => file.fileName);

      this.totalManualUploads = this.storageMetadata.filter(
        file => file.metadata.user_upload === "true"
      ).length;

      this.pipelineStatus = await this.dataloaderRepository._getPipelineStatus(
        this.containerName
      );

      if (this.pipelineStatus == null) {
        this.pipelineStatus = { status: "n/a", message: "", runEnd: "" };
      }
      this.loading = false;
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
      this.errorMessage = null;
      if (!this.fileBlob) {
        this.setErrorState("Please select a file to be uploaded.");
        return;
      }

      if (!this.destinationFileName) {
        this.setErrorState(
          "Please select a destination file name for the upload."
        );
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
    setErrorState(errorMessage) {
      this.errorOccured = true;
      this.errorMessage = errorMessage;
      this.uploadSuccessful = false;
      this.uploading = false;
    }
  }
};
</script>
