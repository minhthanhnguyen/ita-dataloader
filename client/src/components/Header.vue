<template>
  <md-toolbar class="md-primary md-dense dataloader-toolbar">
    <div style="flex: 1; display: flex">
      <h3 class="md-title dataloader-title">ITA Dataloader</h3>
      <div v-if="businessUnits" class="business-unit-dropdown">
        <select @change="updateBusinessUnitContent($event)">
          <option
            v-for="businessUnit in businessUnits"
            v-bind:key="businessUnit.containerName"
            v-bind:value="businessUnit.containerName"
            v-bind:selected="businessUnit.containerName === containerName"
          >{{businessUnit.businessName}}</option>
        </select>
      </div>
    </div>
    <git-hub />
  </md-toolbar>
</template>
<style>
.business-unit-dropdown {
  margin-left: 20px;
  width: 200px;
}

.business-unit-dropdown select {
  font-size: 16px;
  padding: 4px;
}
</style>
<script>
import GitHub from "./GitHub";
export default {
  name: "Header",
  props: ["initialContainerName", "businessUnits", "routeName"],
  components: {
    "git-hub": GitHub
  },
  data() {
    return {
      containerName: this.initialContainerName
    };
  },
  methods: {
    updateBusinessUnitContent(event) {
      this.$router.push({
        name: this.routeName,
        params: {
          containerName: event.target.value
        }
      });
      this.$parent.containerName = event.target.value;
    }
  }
};
</script>