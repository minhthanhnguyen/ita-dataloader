<template>
  <div>
    <dataloader-header v-bind:businessUnitName="businessUnitName" />
    <div class="md-layout md-gutter">
      <div class="md-layout-item md-size-5">
        <div class="nav-btns">
          <md-button class="md-icon-button" @click="goToFileUpload()">
            <md-icon class="fa fa-angle-double-left"></md-icon>
          </md-button>
          <md-button class="md-icon-button url-log-btn" @click="goToUrlIngestLog()">
            <md-icon class="fa fa-bars"></md-icon>
          </md-button>
        </div>
      </div>
      <!-- <md-list>
        <li v-for="country in countries" v-bind:key="country.code" v-bind:value="country.code">
          <div class="layout-item country-code">
            <md-field>
              <label>Code</label>
              <md-input v-model="country.code" disabled></md-input>
            </md-field>
          </div>
          <div class="layout-item">
            <md-field>
              <label>Country</label>
              <md-input v-model="country.name"></md-input>
            </md-field>
          </div>
          <div class="layout-item">
            <md-switch v-model="country.visible"></md-switch>
          </div>
        </li>
      </md-list>-->
      <div v-if="loading" class="loading">loading...</div>
    </div>
  </div>
</template>
<style>
.layout-item {
  display: inline-block;
  margin-right: 20px;
}
.country-code {
  padding-left: 20px;
  width: 50px;
}
</style>
<script>
import Header from "./Header";

export default {
  name: "Config",
  props: ["businessUnitName", "dataloaderRepository"],
  components: {
    "dataloader-header": Header
  },
  async created() {
    this.loading = true;
    this.containerName = this.$route.params['containerName'];
    this.dataSetConfigs = await this.dataloaderRepository._getDataSetConfigs(this.containerName);
    this.loading = false;
  },
  data() {
    return {
      dataSetConfigs: [],
      loading: true,
      containerName: null
    };
  },
  methods: {
    async goToFileUpload() {
      this.$router.push({
        name: "Upload",
        params: {
          containerName: this.containerName,
          dataloaderRepository: this.dataloaderRepository,
          businessUnitName: this.businessUnitName
        }
      });
    }
  }
};
</script>
