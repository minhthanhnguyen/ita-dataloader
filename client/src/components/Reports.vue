<template>
  <div>
    <dataloader-header v-bind:businessUnitName="businessUnitName" />
    <div class="content">
      <dataloader-menu v-bind:containerName="containerName" />
      <div class="sub-content">
        <p v-if="reports.length === 0">No reports have been created yet.</p>
        <md-table
          v-if="reports.length > 0"
          v-model="reports"
          md-sort="name"
          md-sort-order="asc"
          md-card
          md-fixed-header
        >
          <md-table-row slot="md-table-row" slot-scope="{ item }">
            <md-table-cell md-label="Name" md-sort-by="name">
              <a v-bind:href="item.url" target="_blank">{{item.name}}</a>
            </md-table-cell>
          </md-table-row>
        </md-table>
        <div v-if="loading" class="loading">
          <md-progress-bar md-mode="indeterminate"></md-progress-bar>
        </div>
      </div>
    </div>
  </div>
</template>
<style>
.config-text {
  margin-left: 35px;
  width: 1350px;
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
import Menu from "./Menu";
import Header from "./Header";
import beautify from "json-beautify";
import prettyPrintJson from "pretty-print-json";

export default {
  name: "Reports",
  props: ["dataloaderRepository"],
  components: {
    "dataloader-header": Header,
    "dataloader-menu": Menu
  },
  async created() {
    this.loading = true;
    this.containerName = this.$route.params["containerName"];
    this.businessUnits = await this.dataloaderRepository._getBusinessUnits();
    let businessUnit = this.businessUnits.find(
      b => b.containerName === this.containerName
    );
    this.businessUnitName = businessUnit.businessName;
    this.reports = businessUnit.reports;
    this.loading = false;
  },
  data() {
    return {
      loading: true,
      containerName: null,
      businessUnitName: null,
      reports: []
    };
  },
  methods: {}
};
</script>
