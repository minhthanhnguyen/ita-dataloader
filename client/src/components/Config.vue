<template>
  <div>
    <dataloader-header v-bind:businessUnitName="businessUnitName" />
    <div class="md-layout md-gutter">
      <div class="md-layout-item md-size-5">
        <div class="nav-btns">
          <md-button class="md-icon-button" @click="goToFileUpload()">
            <md-icon class="fa fa-angle-double-left"></md-icon>
          </md-button>
          <md-button class="md-icon-button url-log-btn" @click="goToUrlIngestLog()">
            <md-icon class="fa fa-bars"></md-icon>
          </md-button>
        </div>
      </div>
      <div class="md-layout-item md-size-75"></div>
      <div class="md-layout-item md-size-10">
        <md-button class="md-secondary md-raised top-btn" @click="saveConfiguration()">
          <label>Save</label>
        </md-button>
      </div>
      <div class="md-layout-item md-size-10">
        <md-button
          v-if="!ingesting"
          class="md-primary md-raised top-btn"
          @click="startIngestProcess()"
        >
          <label>Ingest</label>
        </md-button>
        <div class="ingesting-message">
          <md-progress-spinner v-if="ingesting" md-mode="indeterminate" :md-diameter="30"></md-progress-spinner>
        </div>
      </div>
      <div class="config-text">
        <md-field>
          <textarea v-model="dataloaderConfig" rows="200" cols="195" wrap="off"></textarea>
        </md-field>
      </div>
      <md-dialog-alert
        :md-active.sync="configSaved"
        md-content="The configuration was saved successfully! "
        md-confirm-text="Close"
      />
      <md-dialog-alert
        :md-active.sync="ingestClicked"
        md-content="The ingest process started successfully! View the Log check it's progress."
        md-confirm-text="Close"
      />
      <div v-if="loading" class="loading">loading...</div>
    </div>
  </div>
</template>
<style>
.config-text {
  margin-left: 30px;
}
.ingesting-message {
  margin-top: 20px;
}
</style>
<script>
import Header from "./Header";
import beautify from "json-beautify";

export default {
  name: "Config",
  props: ["dataloaderRepository"],
  components: {
    "dataloader-header": Header
  },
  async created() {
    this.loading = true;
    this.containerName = this.$route.params["containerName"];
    this.dataloaderConfig = beautify(
      await this.dataloaderRepository._getDataloaderConfig(this.containerName),
      null,
      2,
      100
    );
    this.businessUnits = await this.dataloaderRepository._getBusinessUnits();
    this.businessUnitName = this.businessUnits.find(
      b => b.containerName === this.containerName
    ).businessName;
    this.loading = false;
  },
  data() {
    return {
      loading: true,
      containerName: null,
      businessUnitName: null,
      dataloaderConfig: null,
      configSaved: false,
      ingestClicked: false,
      ingesting: false
    };
  },
  methods: {
    async startIngestProcess() {
      this.ingesting = true;
      this.ingestClicked = true;
      await this.dataloaderRepository._startIngestProcess();
      this.ingesting = false;
    },
    async goToFileUpload() {
      this.$router.push({
        name: "Upload",
        params: {
          containerName: this.containerName
        }
      });
    },
    async goToUrlIngestLog() {
      this.$router.push({
        name: "Log",
        params: {
          containerName: this.containerName
        }
      });
    },
    async saveConfiguration() {
      let configSaveResponse = await this.dataloaderRepository._saveDataloaderConfig(
        this.dataloaderConfig,
        this.containerName
      );

      if (configSaveResponse.status === 200) this.configSaved = true;
    }
  }
};
</script>
