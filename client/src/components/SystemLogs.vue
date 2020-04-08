<template>
  <div>
    <dataloader-header
      v-if="containerName"
      :businessUnits="businessUnits"
      :initialContainerName="containerName"
      :updateContainerFn="updateContainer"
    />
    <div class="content">
      <dataloader-menu :containerName="containerName" />
      <div class="sub-content window-panes">
        <log
          header="Automated Ingest Log"
          :visible="showAutomatedIngest"
          :status="automatedIngestStatus"
          :loading="loadingAutomatedIngestLog"
          :stopFn="stopAutomatedIngest"
          :clearFn="clearAutomatedIngestLog"
          :refreshFn="refreshAutomatedIngestLog"
        />
        <log
          header="Manual Ingest Log"
          :visible="showManualIngest"
          :status="manualIngestStatus"
          :loading="loadingManualIngestLog"
          :clearFn="clearManualIngestLog"
          :refreshFn="refreshManualIngestLog"
        />
      </div>
    </div>
  </div>
</template>
<style>
.window-panes {
  display: flex;
}
</style>
<script>
import Menu from "./Menu";
import Header from "./Header";
import Log from "./Log";

export default {
  name: "SystemLogs",
  props: ["repository"],
  components: {
    "dataloader-header": Header,
    "dataloader-menu": Menu,
    log: Log
  },
  async created() {
    this.loadingAutomatedIngestLog = true;
    this.loadingManualIngestLog = true;
    this.containerName = this.$route.params["containerName"];
    await this.updateBusinessUnitContent();
    await this.refreshAutomatedIngestLog(0);
    await this.refreshManualIngestLog(0);
    this.loadingAutomatedIngestLog = false;
    this.loadingManualIngestLog = false;
  },
  data() {
    return {
      loadingAutomatedIngestLog: true,
      loadingManualIngestLog: true,
      containerName: null,
      businessUnits: [],
      automatedIngestStatus: this.defaultIngestStatus(),
      manualIngestStatus: this.defaultIngestStatus(),
      showAutomatedIngest: true,
      showManualIngest: true
    };
  },
  methods: {
    async refreshAutomatedIngestLog(waitMs) {
      this.loadingAutomatedIngestLog = true;
      this.automatedIngestStatus = await this.repository._getAutomatedIngestStatus(
        this.containerName
      );
      if (this.automatedIngestStatus.logItems === null)
        this.automatedIngestStatus = this.defaultIngestStatus();
      await this.sleep(waitMs);
      this.loadingAutomatedIngestLog = false;
    },
    async refreshManualIngestLog(waitMs) {
      this.loadingManualIngestLog = true;
      this.manualIngestStatus = await this.repository._getManualIngestStatus(
        this.containerName
      );
      if (this.manualIngestStatus.logItems === null)
        this.manualIngestStatus = this.defaultIngestStatus();
      await this.sleep(waitMs);
      this.loadingManualIngestLog = false;
    },
    clearAutomatedIngestLog() {
      this.repository._clearAutomatedIngestStatus(this.containerName);
      this.automatedIngestStatus.isDone = null;
      this.automatedIngestStatus.logItems = [];
    },
    clearManualIngestLog() {
      this.repository._clearManualIngestStatus(this.containerName);
      this.manualIngestStatus.isDone = null;
      this.manualIngestStatus.logItems = [];
    },
    stopAutomatedIngest() {
      this.repository._stopAutomatedIngestProcess(this.containerName);
    },
    defaultIngestStatus() {
      return {
        isDone: null,
        logItems: []
      };
    },
    async updateBusinessUnitContent() {
      this.businessUnits = await this.repository._getBusinessUnits();
      this.businessUnitName = this.businessUnits.find(
        b => b.containerName === this.containerName
      ).businessName;
      await this.refreshManualIngestLog();
      await this.refreshAutomatedIngestLog();
    },
    async updateContainer(containerName) {
      this.containerName = containerName;
      await this.updateBusinessUnitContent();
      this.$forceUpdate();
    },
    async sleep(ms) {
      return new Promise(resolve => setTimeout(resolve, ms));
    },
    paneClass(visible) {
      if (visible) {
        return "pane open-pane";
      } else {
        return "pane closed-pane";
      }
    }
  }
};
</script>
