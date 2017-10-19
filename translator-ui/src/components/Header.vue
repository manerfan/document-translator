<template>
  <header>
    <el-menu :default-active="active" class="el-menu-demo" mode="horizontal" :router="true"
             background-color="#545c64"
             text-color="#fff"
             active-text-color="#ffd04b">
      <el-menu-item index="/">DocTranslator</el-menu-item>
      <a class="el-menu-item item-right" target="_blank" href="https://manerfan.github.io/document-translator/">
        <i class="fa fa-github" aria-hidden="true"></i>
      </a>
      <el-menu-item index="/trans/doc" class="item-right">文档翻译</el-menu-item>
      <el-menu-item index="/trans/sbd" class="item-right">文章断句</el-menu-item>
      <el-menu-item index="/trans/text" class="item-right">文本翻译</el-menu-item>
    </el-menu>
    <div class="line"></div>
  </header>
</template>

<script>
  export default {
    props: ['active']
  }
</script>

<style scoped lang="scss">
  header {
    margin-bottom: 2rem;
  }

  .item-right {
    float: right !important;
  }

  a.el-menu-item {
    background-color: #ea6f5a;
    i {
      color: ghostwhite;
    }

    &:hover, &:focus {
      background-color: #d65842;
    }

    .fa-github {
      font-size: 1.5rem;
      line-height: 1.5rem;
    }
  }
</style>
