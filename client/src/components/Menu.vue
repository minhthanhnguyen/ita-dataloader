<template>
  <nav class="nav-menu">
    <md-list class="menu-list-items">
      <md-list-item class="menu-li" @click="goTo('Upload')">
        <md-icon id="menu-icon">arrow_upward</md-icon>
        <span class="md-list-item-text">Upload</span>
      </md-list-item>
      <md-list-item class="menu-li" @click="goTo('Config')">
        <md-icon id="menu-icon">settings_applications</md-icon>
        <span class="md-list-item-text">Configure</span>
      </md-list-item>
      <md-list-item class="menu-li" @click="goTo('Log')">
        <md-icon id="menu-icon">menu_book</md-icon>
        <span class="md-list-item-text">Log</span>
      </md-list-item>
      <md-list-item class="menu-li" @click="goTo('Reports')">
        <md-icon id="menu-icon">pie_chart</md-icon>
        <span class="md-list-item-text">Reports</span>
      </md-list-item>
      <md-list-item class="menu-li" @click="goTo('APIs')">
        <md-icon id="menu-icon">developer_mode</md-icon>
        <span class="md-list-item-text">APIs</span>
      </md-list-item>
    </md-list>
  </nav>
</template>
<style>
.nav-menu {
  background: #8b9197;
  height: 1000px;
  width: 55px;
}

.nav-menu:hover {
  transition: 0.5s;
  width: 156px;
}

.menu-list-items {
  padding: 0;
}

.menu-li {
  background: #8b9197;
}

.menu-li .md-list-item-text {
  color: white;
  margin-left: 10px;
}

#menu-icon {
  color: white;
  margin: 0 5px 0 0;
}
</style>
<script>
export default {
  name: "Menu",
  props: {
    containerName: String
  },
  methods: {
    goTo(routeName) {
      this.$router.push({
        name: routeName,
        params: {
          containerName: this.containerName
        }
      });
    }
  }
};
</script>