<template>
  <div>
    <dataloader-header v-bind:businessUnitName="businessUnitName" />
    <div class="content">
      <dataloader-menu v-bind:containerName="containerName" />
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
          <a @click="refreshLog()" href="#">Refresh</a>
        </div>

        <div class="log-details">
          <md-table v-model="injestStatus.log" md-sort="time" md-sort-order="asc">
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
  props: ["dataloaderRepository"],
  components: {
    "dataloader-header": Header,
    "dataloader-menu": Menu
  },
  async created() {
    this.loading = true;
    this.containerName = this.$route.params["containerName"];
    this.businessUnits = await this.dataloaderRepository._getBusinessUnits();
    this.businessUnitName = this.businessUnits.find(
      b => b.containerName === this.containerName
    ).businessName;
    this.injestStatus = await this.dataloaderRepository._getIngestStatus(
      this.containerName
    );

    if (!this.injestStatus) {
      this.injestStatus = this.defaultInjectStatus();
    }

    this.loading = false;
  },
  data() {
    return {
      loading: true,
      containerName: null,
      businessUnitName: null,
      injestStatus: this.defaultInjectStatus()
    };
  },
  methods: {
    async refreshLog() {
      this.loading = true;
      this.injestStatus = await this.dataloaderRepository._getIngestStatus(
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
    }
  }
};
</script>
