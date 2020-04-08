<template>
  <div id="container">
    <router-view />
  </div>
</template>

<script>
import Menu from "@/components/Menu";

export default {
  name: "App"
};
</script>

<style lang="scss">
@import "~vue-material/dist/theme/engine"; // Import the theme engine
@include md-register-theme(
  "default",
  (
    primary: #3e4853,
    // The primary color of your brand
  )
);
@import "~vue-material/dist/theme/all"; // Apply the theme

.top-btn {
  margin: 16px 32px 0 0;
}

.loading {
  margin-left: 30px;
  margin-top: 16px;
}

.nav-menu {
  height: 500px;
  margin-right: 16px;
}

.content {
  display: flex;
}

.sub-content {
  min-width:  1000px;
  width: 100%;
  padding-top: 12px;
}

</style>
