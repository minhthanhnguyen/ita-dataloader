<template>
  <md-toolbar class="md-primary" md-elevation="1">
    <h3
      v-if="businessUnitName"
      class="md-title"
      style="flex: 1"
    >ITA Dataloader - {{businessUnitName}}</h3>
    <h3 v-else class="md-title" style="flex: 1">ITA Dataloader</h3>
    <git-hub />
  </md-toolbar>
</template>
<script>
import GitHub from "./GitHub";
export default {
  name: "Header",
  props: {
    businessUnitName: String
  },
  components: {
    "git-hub": GitHub
  }
};
</script>