<template>
  <md-toolbar class="md-primary" md-elevation="1">
    <h3 class="md-title" style="flex: 1">ITA Dataloader</h3>
    <git-hub />
  </md-toolbar>
</template>
<script>
import GitHub from "./GitHub";
export default {
  name: "Header",
  components: {
    "git-hub": GitHub
  }
};
</script>