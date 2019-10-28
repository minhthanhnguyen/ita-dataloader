<template>
  <div id="container">
    <router-view />
  </div>
</template>

<script>
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
  margin-top: 16px;
}

.loading {
  margin-left: 30px;
  margin-top: 16px;
}

.nav-menu {
  height: 500px;
  margin-right: 16px;
}

.spinner {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.stat {
  font-size: 12px;
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
