<template>
  <div class="home">
    <Header active="/"></Header>
    <el-main><transition name="fade" mode="out-in"><router-view/></transition></el-main>
    <Footer></Footer>
  </div>
</template>

<script>
  import Header from './Header.vue'
  import Footer from './Footer.vue'

  export default {
    components: {
      Header,
      Footer
    },
    name: 'Layout'
  }
</script>
