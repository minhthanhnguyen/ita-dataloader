<template>
  <div>
    <dataloader-header />
    <div class="md-layout md-gutter">
      <div class="md-layout-item md-size-5">
        <md-button class="md-icon-button config-btn" @click="goToConfig()">
          <md-icon class="fa fa-cog"></md-icon>
        </md-button>
      </div>
      <div class="md-layout-item md-size-20">
        <md-field>
          <label>Business Unit</label>
          <md-select v-model="containerName">
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
        <md-field>
          <label>Destination file name</label>
          <md-input v-model="destinationFileName"></md-input>
        </md-field>
      </div>
      <div class="md-layout-item md-size-10">
        <md-button class="md-secondary md-raised top-btn" @click="uploadFile()">Upload</md-button>
      </div>
    </div>
    <div v-if="loading" class="loading">loading...</div>
    <div class="user-feedback">
      <div class="error" v-if="errorOccured">
        <ul>
          <li v-for="message in errorMessages" v-bind:key="message">{{message}}</li>
        </ul>
      </div>
      <div v-if="uploading">Uploading...</div>
      <div v-if="uploadSuccessful" class="success">
        <p>{{this.originalFileName}} was successfully uploaded as {{this.destinationFileName}}!</p>
      </div>
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
</style>
<script>
import { readUploadedFileAsArrayBuffer } from "./FileHelper";
import Header from "./Header";
import { read, utils } from "xlsx";

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
    this.containerName = this.businessUnits[0].containerName;
    this.loading = false;
  },
  data() {
    return {
      containerName: null,
      businessUnits: [],
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
    onFileSelection(event) {
      this.fileBlob = event[0];
      this.originalFileName = event[0].name;
    },
    async uploadFile() {
      this.uploading = true;
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

      let fileArrayBuffer = await readUploadedFileAsArrayBuffer(this.fileBlob);
      const message = await this.fileRepository._save(
        this.destinationFileName,
        fileArrayBuffer,
        this.containerName
      );
      this.uploadSuccessful = true;
      this.uploading = false;
    },
    setErrorState(errorMessages) {
      this.errorOccured = true;
      this.errorMessages = errorMessages;
      this.uploadSuccessful = false;
      this.uploading = false;
    },
    goToConfig() {
      this.$router.push({
        name: "Config",
        params: { containerName: this.containerName, dataloaderRepository: this.dataloaderRepository }
      });
    }
  }
};
</script>
