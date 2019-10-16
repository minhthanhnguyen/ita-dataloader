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
        <md-button
          v-if="!editing"
          class="md-secondary md-raised top-btn"
          @click="editConfiguration()"
        >
          <label>Edit</label>
        </md-button>
        <md-button
          v-if="editing"
          class="md-secondary md-raised top-btn"
          @click="saveConfiguration()"
        >
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
        <div v-if="ingesting" class="spinner">
          <md-progress-spinner md-mode="indeterminate" :md-diameter="30"></md-progress-spinner>
        </div>
      </div>
      <div class="md-layout md-alignment-top-center">
        <div class="config-text">
          <pre v-if="!editing" id="pretty-config-json"></pre>
          <md-field v-else>
            <textarea v-model="dataloaderConfigBeautified" rows="200" cols="195" wrap="off"></textarea>
          </md-field>
        </div>
        <md-dialog-alert
          :md-active.sync="configSaved"
          md-content="The configuration was saved successfully! "
          md-confirm-text="Close"
        />
        <md-dialog-alert
          :md-active.sync="ingestClicked"
          md-content="The ingest process was started successfully! To view its progress, see the log."
          md-confirm-text="Close"
        />
      </div>
    </div>
    <div v-if="loading" class="loading">
      <md-progress-bar md-mode="indeterminate"></md-progress-bar>
    </div>
  </div>
</template>
<style>
.config-text {
  margin-left: 35px;
}
.json-key {
  color: brown;
}
.json-value {
  color: navy;
}
.json-boolean {
  color: teal;
}
.json-string {
  color: olive;
}
</style>
<script>
import Header from "./Header";
import beautify from "json-beautify";
import prettyPrintJson from "pretty-print-json";

export default {
  name: "Config",
  props: ["dataloaderRepository"],
  components: {
    "dataloader-header": Header
  },
  async created() {
    this.loading = true;
    this.containerName = this.$route.params["containerName"];
    this.dataloaderConfig = await this.dataloaderRepository._getDataloaderConfig(
      this.containerName
    );
    this.dataloaderConfigBeautified = beautify(
      this.dataloaderConfig,
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
  updated() {
    this.$nextTick(function() {
      if (document.getElementById("pretty-config-json"))
        document.getElementById(
          "pretty-config-json"
        ).innerHTML = prettyPrintJson.toHtml(this.dataloaderConfig);
    });
  },
  data() {
    return {
      loading: true,
      containerName: null,
      businessUnitName: null,
      dataloaderConfig: null,
      dataloaderConfigBeautified: null,
      configSaved: false,
      ingestClicked: false,
      ingesting: false,
      editing: false
    };
  },
  methods: {
    async startIngestProcess() {
      this.ingesting = true;
      this.ingestClicked = true;
      await this.dataloaderRepository._startIngestProcess(this.containerName);
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
      this.dataloaderConfig = JSON.parse(this.dataloaderConfigBeautified);
      let configSaveResponse = await this.dataloaderRepository._saveDataloaderConfig(
        this.dataloaderConfig,
        this.containerName
      );

      if (configSaveResponse.status === 200) this.configSaved = true;
      this.editing = false;
    },
    editConfiguration() {
      this.editing = true;
    }
  }
};
</script>
