<template>
  <div class="home">
    <h1>可靠、快捷、简易</h1>
    <h4>保持原始办公文档的排版</h4>
    <el-row :gutter="80" style="margin: 5rem 10rem">
      <el-col :span="8">
        <p class="about-step-num">1</p>
        <h4>DocTranslator使用强大的百度翻译功能</h4>
        <p class="grid-content">
          DocTranslator依赖不断完善的<a href="http://api.fanyi.baidu.com/" target="_blank">百度翻译</a>服务能力，以此处理文档中的文本，并将其翻译成您所需要的语言。
        </p>
      </el-col>
      <el-col :span="8">
        <p class="about-step-num">2</p>
        <h4>已翻译的文本将被重新插入到文档中，并保持原始排版。</h4>
        <p class="grid-content">
          无需再从文档中复制/粘贴文本。Doc Translator智能地获取并重新插入文本至其原本位置。支持绝大部分编码格式。
        </p>
      </el-col>
      <el-col :span="8">
        <p class="about-step-num">3</p>
        <h4>针对聊天中的表情符号及@mention进行特殊处理。</h4>
        <p class="grid-content">
          专门针对聊天中的表情符号及@mention进行处理，免除误翻带来的烦恼。
        </p>
      </el-col>
    </el-row>
    <div class="statistics" v-show="showStatistics">
      <h1>累计翻译</h1>
      <el-row :gutter="40">
        <el-col :span="6">
          <el-card :body-style="{padding: 0}">
            <h3>文本</h3>
            <h1>{{num(textTransTimes)}}次</h1>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card :body-style="{padding: 0}">
            <h3>文档</h3>
            <h1>{{num(docTransTimes)}}次</h1>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card :body-style="{padding: 0}">
            <h3>断句</h3>
            <h1>{{num(sbdTimes)}}次</h1>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card :body-style="{padding: 0}">
            <h3>总字符</h3>
            <h1>{{byte(textTransNum)}}</h1>
          </el-card>
        </el-col>
      </el-row>
    </div>
    <el-button type="primary" plain>
      <router-link :to="{path: '/trans/text'}">文本翻译</router-link>
    </el-button>
    <el-button style="font-size: 2rem;" type="danger" icon="el-icon-arrow-right">
      <router-link :to="{path: '/trans/doc'}">现在翻译</router-link>
    </el-button>
    <el-button type="primary" plain>
      <router-link :to="{path: '/trans/sbd'}">文章断句</router-link>
    </el-button>
  </div>
</template>

<script>
  import http from '../tools/http'

  export default {
    name: 'Home',
    data () {
      return {
        from: {
          code: 'auto',
          name: '自动检测'
        },
        to: {
          code: 'zh',
          name: '中文'
        },
        showStatistics: false,
        textTransTimes: 0,
        docTransTimes: 0,
        sbdTimes: 0,
        textTransNum: 0
      }
    },
    created () {
      http({
        url: 'trans/statistics',
        method: 'get'
      }).then(({data}) => {
        this.textTransTimes = data.textTransTimes
        this.docTransTimes = data.docTransTimes
        this.sbdTimes = data.sbdTimes
        this.textTransNum = data.textTransNum

        this.showStatistics = true
      })
    },
    methods: {
      num (n) {
        if (n > 9999) return `${(n / 10000).toFixed(1)}万`
        return n
      },
      byte (b) {
        if (b >= 1024 * 1024 * 1024) return `${(b / 1024 / 1024 / 1024).toFixed(1)} GB`
        if (b >= 1024 * 1024) return `${(b / 1024 / 1024).toFixed(1)} MB`
        if (b >= 1024) return `${(b / 1024).toFixed(1)} KB`
        return `${b} Bytes`
      }
    }
  }
</script>

<style scoped>
  .about-step-num {
    -webkit-box-sizing: border-box;
    -moz-box-sizing: border-box;
    box-sizing: border-box;
    display: inline-block;
    color: #e65028;
    border: 1px solid #e65028;
    font-size: 1.375em;
    font-size: 1.625em;
    padding: 0.375em 0.6875em;
    font-weight: 600;
    -webkit-border-radius: 50%;
    -moz-border-radius: 50%;
    border-radius: 50%;
  }

  .statistics {
    margin: 0 2rem 5rem;
  }
</style>
