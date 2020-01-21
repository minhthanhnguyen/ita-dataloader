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
      <div class="sub-content">
        <div class="ingest-stats">
          <span class="stat">
            <strong>INGESTING:</strong>
            <span>{{injestStatus.ingesting}}</span>
          </span>
          <span class="stat">
            <strong>QUEUED:</strong>
            <span>{{injestStatus.datasetsQueued}}</span>
          </span>
          <span class="stat">
            <strong>COMPLETED:</strong>
            <span>{{injestStatus.datasetsCompleted}}</span>
          </span>
          <md-button class="md-dense refresh-btn" @click="refreshLog()">Refresh</md-button>
        </div>

        <div class="log-details">
          <md-table
            v-if="injestStatus.log"
            v-model="injestStatus.log"
            md-sort="time"
            md-sort-order="asc"
          >
            <md-table-row slot-scope="{ item }" slot="md-table-row">
              <md-table-cell md-label="Timestamp">{{item.time}}</md-table-cell>
              <md-table-cell md-label="Message">{{item.message}}</md-table-cell>
            </md-table-row>
          </md-table>
        </div>

        <div v-if="loading" class="loading">
          <md-progress-bar md-mode="indeterminate"></md-progress-bar>
        </div>
      </div>
    </div>
  </div>
</template>
<style>
.ingest-stats {
  margin-top: 15px;
}

.log-details {
  margin-top: 10px;
  margin-left: 20px;
}

.md-table-cell {
  white-space: nowrap;
}
</style>
<script>
import Menu from "./Menu";
import Header from "./Header";

export default {
  name: "Log",
  props: ["repository"],
  components: {
    "dataloader-header": Header,
    "dataloader-menu": Menu
  },
  async created() {
    this.loading = true;
    this.containerName = this.$route.params["containerName"];
    await this.updateBusinessUnitContent();
    this.loading = false;
  },
  data() {
    return {
      loading: true,
      containerName: null,
      businessUnits: [],
      injestStatus: this.defaultInjectStatus()
    };
  },
  methods: {
    async refreshLog() {
      this.loading = true;
      this.injestStatus = await this.repository._getIngestStatus(
        this.containerName
      );
      this.loading = false;
    },
    defaultInjectStatus() {
      return {
        processing: false,
        totalUrlCallsQueued: 0,
        processedUrlCalls: 0,
        log: []
      };
    },
    async updateBusinessUnitContent() {
      this.businessUnits = await this.repository._getBusinessUnits();
      this.businessUnitName = this.businessUnits.find(
        b => b.containerName === this.containerName
      ).businessName;
      this.injestStatus = await this.repository._getIngestStatus(
        this.containerName
      );

      if (!this.injestStatus) {
        this.injestStatus = this.defaultInjectStatus();
      }
    },
    async updateContainer(containerName) {
      this.containerName = containerName;
      await this.updateBusinessUnitContent();
      this.$forceUpdate()
    }
  }
};
</script>
