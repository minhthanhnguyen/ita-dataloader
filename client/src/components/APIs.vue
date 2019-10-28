<template>
  <div>
    <dataloader-header v-bind:businessUnitName="businessUnitName" />
    <div class="content">
      <dataloader-menu v-bind:containerName="containerName" />
      <div class="sub-content">
        <section>
          <h1 class="md-title">Storage Containers</h1>
          <a href="/api/storage-containers" target="_blank">/api/storage-containers</a>
          <p>Returns an array of $containerName</p>
        </section>
        <section>
          <h1 class="md-title">Container Metadata</h1>
          <p>/api/container-metadata?containerName=$containerName</p>
          <p>Returns the metadata associated with a given storage container: $containerName, $fileName</p>
        </section>
        <section>
          <h1 class="md-title">FTA Tariff Rates</h1>
          <p>/api/extract/fta/tariff-rates?containerName=$containerName&amp;fileName=$fileName</p>
          <p>Returns tariff rates for the given file with Rule of Origin applied</p>
        </section>
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
  name: "APIs",
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
    this.loading = false;
  },
  data() {
    return {
      loading: true,
      containerName: null,
      businessUnitName: null
    };
  },
  methods: {}
};
</script>
