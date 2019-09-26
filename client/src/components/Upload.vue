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
      <div class="md-layout-item md-size-30">
        <md-field>
          <label>Select file</label>
          <md-file v-model="fileName" @md-change="onFileSelection($event)"></md-file>
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
        <p>{{this.fileName}} was uploaded successfully!</p>
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
    businessUnitRepository: Object
  },
  components: {
    "dataloader-header": Header
  },
  async created() {
    this.loading = true;
    this.businessUnits = await this.businessUnitRepository._getBusinessUnits();
    this.containerName = this.businessUnits[0].containerName;
    this.loading = false;
  },
  data() {
    return {
      containerName: null,
      businessUnits: [],
      fileName: null,
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
      this.errorMessages = [];
      this.uploadSuccessful = false;
      this.fileType = null;
      if (event[0].name.endsWith(".xlsx")) {
        this.fileType = "xlsx";
        this.fileBlob = event[0];
      } else if (event[0].name.endsWith(".csv")) {
        this.fileType = "csv";
        this.fileBlob = event[0];
      } else {
        this.fileType = null;
        this.errorOccured = true;
      }
    },
    async uploadFile() {
      this.uploading = true;
      this.errorMessages = [];
      if (!this.fileType) {
        this.setErrorState([
          "Please select a .xlsx or .csv file to be uploaded."
        ]);
        return;
      }

      let csv = null;
      var headers = [];
      let fileArrayBuffer = await readUploadedFileAsArrayBuffer(this.fileBlob);
      if (this.fileType === "xlsx") {
        let workbook = read(new Uint8Array(fileArrayBuffer), { type: "array" });
        let workSheetName = workbook.SheetNames[0];
        let workSheet = workbook.Sheets[workSheetName];
        var range = utils.decode_range(workSheet["!ref"]);
        for (var colNum = range.s.c; colNum < range.e.c; colNum++) {
          const cell = workSheet[utils.encode_cell({ r: 0, c: colNum })];
          if (!cell) {
            headers.push(null);
            continue;
          }
          headers.push(cell.v);
        }

        csv = utils.sheet_to_csv(workSheet);
      }

      if (this.fileType === "csv") {
        csv = new TextDecoder("utf-8").decode(new Uint8Array(fileArrayBuffer));
        headers = csv
          .substring(0, csv.indexOf("\n"))
          .replace(/"/g, "")
          .split(",");

      }

      const message = await this.tariffRepository._saveTariffs(
        this.countryCode,
        csv
      );

      if (message == "success") {
        this.uploadSuccessful = true;
        this.errorOccured = false;
      } else {
        this.setErrorState([message]);
      }

      this.uploading = false;
    },
    setErrorState(errorMessages) {
      this.errorOccured = true;
      this.errorMessages = errorMessages;
      this.uploadSuccessful = false;
      this.uploading = false;
    },
    goToConfig() {
      this.$router.push({ name: "Config" });
    }
  }
};
</script>
