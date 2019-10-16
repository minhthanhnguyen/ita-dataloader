<template>
  <div>
    <dataloader-header v-bind:businessUnitName="businessUnitName" />
    <div class="md-layout md-gutter">
      <div class="md-layout-item md-size-10">
        <div class="nav-btns">
          <md-button class="md-icon-button" @click="goToFileUpload()">
            <md-icon class="fa fa-angle-double-left"></md-icon>
          </md-button>
          <md-button class="md-icon-button url-log-btn" @click="goToUrlIngestConfig()">
            <md-icon class="fa fa-cog"></md-icon>
          </md-button>
          <md-button class="md-icon-button" @click="refreshLog()">
            <md-icon class="fa fa-refresh"></md-icon>
          </md-button>
        </div>
      </div>
      <div class="md-layout-item md-size-90">
        <div class="ingest-stats">
          <span class="stats">
            <strong>INGESTING:</strong>
            <span>{{injestStatus.ingesting}}</span>
          </span>
          <span class="stats">
            <strong>QUEUED:</strong>
            <span>{{injestStatus.datasetsQueued}}</span>
          </span>
          <span class="stats">
            <strong>COMPLETED:</strong>
            <span>{{injestStatus.datasetsCompleted}}</span>
          </span>
        </div>
      </div>
    </div>

    <div class="log-details">
      <md-table v-model="injestStatus.log" md-sort="time" md-sort-order="asc">
        <md-table-row slot-scope="{ item }" slot="md-table-row">
          <md-table-cell md-label="Timestamp">{{item.time}}</md-table-cell>
          <md-table-cell md-label="Message">{{item.message}}</md-table-cell>
        </md-table-row>
      </md-table>
    </div>

    <div v-if="loading" class="loading">loading...</div>
  </div>
</template>
<style>
.layout-item {
  display: inline-block;
  margin-right: 20px;
}
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
import Header from "./Header";

export default {
  name: "Log",
  props: ["dataloaderRepository"],
  components: {
    "dataloader-header": Header
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
    async goToFileUpload() {
      this.$router.push({
        name: "Upload",
        params: {
          containerName: this.containerName
        }
      });
    },
    async goToUrlIngestConfig() {
      this.$router.push({
        name: "Config",
        params: {
          containerName: this.containerName
        }
      });
    },
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
