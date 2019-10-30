<template>
  <div>
    <dataloader-header />
    <div class="content">
      <dataloader-menu />
      <div class="sub-content">
        <section>
          <h1 class="md-title">Storage Containers</h1>
          <a href="/api/storage-containers" target="_blank">/api/storage-containers</a>
          <p>Returns an array of strings. Each string represents a $containerName.</p>
        </section>
        <section>
          <h1 class="md-title">Container Metadata</h1>
          <p>/api/container-metadata?containerName=$containerName</p>
          <p>Returns the metadata associated with a given storage container:</p>
          <ul>
            <li>$fileName (string): The name of the file in storage.</li>
            <li>$url (string): The url to access the file in storage.</li>
            <li>$size (integer): The size of the file in bytes.</li>
            <li>$containerName (string): The storage container a file is located in.</li>
            <li>$uploadedAt (string): The date and time a file was uploaded in ISO 8601 format (yyyy-MM-ddThh:mm:ssZ)</li>
            <li>$metadata.uploaded_by: The email address of the user that uploaded the file.</li>
            <li>$metadata.user_upload (string): Either "true" or "false"; signifies whether a file was uploaded by a user, or ingested from the configuration.</li>
          </ul>
        </section>
        <section>
          <h1 class="md-title">FTA Tariff Rates</h1>
          <p>/api/extract/fta/tariff-rates?fileName=$fileName</p>
          <p>$fileName (string): Any file name from the fta-tariff-rates storage container</p>
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
