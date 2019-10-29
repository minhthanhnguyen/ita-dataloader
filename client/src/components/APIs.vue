<template>
  <div>
    <dataloader-header />
    <div class="content">
      <dataloader-menu />
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
          <p>Returns tariff rates for the given file with Rules of Origin applied</p>
        </section>
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
  }
};
</script>
