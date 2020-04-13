<template>
  <div :class="paneClass(visible)">
    <div class="pane-header">
      <span class="pane-operation" @click="visible = false" v-if="visible">
        <md-icon>remove</md-icon>
      </span>
      <span class="pane-operation" @click="visible = true" v-if="!visible">
        <md-icon>add</md-icon>
      </span>
      <span v-if="visible" class="md-title">{{header}}</span>
    </div>
    <div v-if="visible">
      <div class="ingest-header">
        <div class="status">
          <strong>STATUS:</strong>
          <span v-if="status.isDone === true">Complete</span>
          <span v-else-if="status.isDone === false">Processing</span>
          <span v-else>n/a</span>
        </div>
        <div class="log-operations">
          <md-button v-if="stopFn" class="md-dense top-btn md-raised md-accent" @click="stopFn">Stop</md-button>
          <md-button class="md-dense md-raised top-btn" @click="clearFn">Clear</md-button>
          <md-button class="md-dense top-btn md-raised md-primary" @click="refreshFn(500)">Refresh</md-button>
        </div>
      </div>
      <md-table
        v-if="!loading && status.logItems"
        v-model="status.logItems"
        md-sort="time"
        md-sort-order="asc"
      >
        <md-table-row slot-scope="{ item }" slot="md-table-row">
          <md-table-cell md-label="Timestamp" md-sort-by="time">{{item.time}}</md-table-cell>
          <md-table-cell md-label="Message">{{item.message}}</md-table-cell>
        </md-table-row>
      </md-table>
      <div v-if="loading" class="loading">
        <md-progress-bar md-mode="indeterminate"></md-progress-bar>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: "Log",
  props: [
    "header",
    "visible",
    "status",
    "loading",
    "stopFn",
    "clearFn",
    "refreshFn"
  ],
  methods: {
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
<style>
.ingest-header {
  display: flex;
  justify-content: space-between;
  height: 72px;
}

.status {
  padding-left: 34px;
}

.pane-header {
  display: flex;
}

.pane-operation {
  cursor: pointer;
  padding-right: 12px;
}

.open-pane {
  min-width: 50%;
}

.pane {
  padding: 0 22px 0 22px;
}

.closed-pane {
  min-width: 10px;
}
</style>