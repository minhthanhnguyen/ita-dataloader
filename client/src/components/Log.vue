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
    this.loading = false;
  },
  data() {
    return {
      loading: true,
      containerName: null,
      businessUnitName: null
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
    }
  }
};
</script>
