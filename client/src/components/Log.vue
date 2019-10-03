<template>
  <div>
    <dataloader-header v-bind:businessUnitName="businessUnitName" />
    <div class="md-layout md-gutter">
      <div class="md-layout-item md-size-5">
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
      <div class="md-layout-item md-size-95"></div>
    </div>

    <div class="log-details">
      <h3>Log Details for Latest Ingestion:</h3>
      <p>
        <strong>Processing:</strong>
        <span>{{injestStatus.processing}}</span>
      </p>
      <p>
        <strong>Total datasets queued:</strong>
        <span>{{injestStatus.totalUrlCallsQueued}}</span>
      </p>
      <p>
        <strong>Total datasets saved:</strong>
        <span>{{injestStatus.processedUrlCalls}}</span>
      </p>
      <ul>
        <li
          v-for="message in injestStatus.log"
          v-bind:key="message"
          v-bind:value="message"
        >{{message}}</li>
      </ul>
    </div>

    <div v-if="loading" class="loading">loading...</div>
  </div>
</template>
<style>
.layout-item {
  display: inline-block;
  margin-right: 20px;
}

.log-details {
  margin-left: 20px;
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
    this.loading = false;
  },
  data() {
    return {
      loading: true,
      containerName: null,
      businessUnitName: null,
      injestStatus: {
        processing: false,
        totalUrlCallsQueued: 0,
        processedUrlCalls: 0,
        log: []
      }
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
    }
  }
};
</script>
