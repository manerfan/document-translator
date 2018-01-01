<template>
  <div class="home">
    <h2>DocTranslator使用强大的百度翻译功能</h2>
    <h4>DocTranslator依赖不断完善的<a href="http://api.fanyi.baidu.com/" target="_blank">百度翻译</a>服务能力，以此处理文档中的文本，并将其翻译成您所需要的语言。
    </h4>
    <el-row style="margin: 2rem 2rem">
      <el-col :span="24">
        <el-cascader
          placeholder="试试搜索"
          :options="from.option"
          :show-all-levels="false"
          :filterable="true"
          expand-trigger="hover"
          v-model="from.language"
        ></el-cascader>
        <el-button type="text" disabled @click.stop="exchangeLanguage">
          <i class="fa fa-exchange" aria-hidden="true"></i>
        </el-button>
        <el-cascader
          placeholder="试试搜索"
          :options="to.option"
          :show-all-levels="false"
          :filterable="true"
          expand-trigger="hover"
          v-model="to.language"
        ></el-cascader>
      </el-col>
    </el-row>
    <el-upload style="width: 360px; margin: auto;"
               element-loading-text="拼命翻译中"
               element-loading-spinner="el-icon-loading"
               element-loading-background="rgba(0, 0, 0, 0.5)"
               drag
               action="/api/trans/doc"
               name="file"
               accept=".txt, .docx, .pptx, .xlsx"
               :data="uploadData"
               :on-preview="fileDownload"
               :on-error="uploadError"
               :before-upload="beforeUpload"
               :on-success="uploadSeccess">
      <i class="el-icon-upload"></i>
      <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      <div class="el-upload__tip" slot="tip">
        只能上传<code style="color: #67C23A;">txt/docx/pptx/xlsx</code>
        文件，且不能超过<code style="color: #FA5555;">2MB</code>
      </div>
      <div class="el-upload__tip" slot="tip">
        点击文件名下载
      </div>
    </el-upload>
  </div>
</template>

<script>
  import _ from 'lodash'

  import options from '../tools/languages'

  const fromOptions = _.cloneDeep(options)
  const toOptions = _.cloneDeep(options)
  toOptions.shift()

  let validExt = new Set(['txt', 'docx', 'pptx', 'xlsx'])

  let downloadUrl = (url) => {
    let link = document.createElement('a')
    link.href = url
    link.target = '_blank'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  }

  export default {
    name: 'DocTranslator',
    data () {
      return {
        from: {
          option: fromOptions,
          language: ['auto']
        },
        to: {
          option: toOptions,
          language: ['zh']
        },
        uploading: null
      }
    },
    computed: {
      uploadData () {
        return {
          from: _.cloneDeep(this.from.language).pop(),
          to: _.cloneDeep(this.to.language).pop()
        }
      }
    },
    methods: {
      fileDownload (file) {
        let resp = file.response
        if (_.isUndefined(resp) || _.isUndefined(resp.data)) {
          return this.$message({
            message: '文件已过期',
            type: 'warning'
          })
        }

        downloadUrl(`/api/trans/doc/${resp.data.key}?filename=${resp.data.filename}`)
      },
      uploadError (err, file, fileList) {
        console.error(err)
        this.$message({
          message: '上传失败，可能文件过大',
          type: 'error'
        })

        if (this.uploading && this.uploading.close) {
          this.uploading.close()
        }
      },
      uploadSeccess (response, file, fileList) {
        this.$message({
          message: '点击文件名下载',
          type: 'success'
        })

        if (this.uploading && this.uploading.close) {
          this.uploading.close()
        }
      },
      beforeUpload (file) {
        let name = file.name

        let ext = name.substr(name.lastIndexOf('.') + 1)
        if (!validExt.has(ext.toLowerCase())) {
          this.$message({
            message: '暂不支持此类型文件',
            type: 'warning'
          })
          return false
        }

        if (file.size > 2 * 1024 * 1024) {
          this.$message({
            message: '文件过大',
            type: 'warning'
          })
          return false
        }

        this.uploading = this.$loading({
          lock: true,
          text: '拼命翻译中',
          spinner: 'el-icon-loading',
          background: 'rgba(255, 255, 255, 0.9)'
        })
        return true
      }
    }
  }
</script>

<style lang="scss">
  .el-upload-list {
    .el-icon-close-tip {
      display: none !important;
    }

    .el-upload-list__item {
      background-color: #f9f9f9;
    }
  }
</style>
