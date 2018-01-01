<template>
  <div class="home">
    <h2>Powered By <a href="http://api.fanyi.baidu.com/" target="_blank">百度翻译</a></h2>
    <h4>针对聊天中的表情符号及@mention进行特殊处理。</h4>
    <el-row :gutter="20" style="margin: 2rem 2rem">
      <el-col class="input-col" :span="12">
        <el-input type="textarea" placeholder="请输入要翻译的文字" size="large" resize="none"
                  :rows="10" :maxlength="2000" v-model="from.text">
        </el-input>
        <el-button class="btn-close" type="text" icon="el-icon-error" v-if="hasText" @click.stop="clear"></el-button>
        <code v-if="!!detectedLanguageName" class="detected-language">检测到{{detectedLanguageName}}</code>
      </el-col>
      <el-col :span="12">
        <el-input type="textarea" size="large" resize="none"
                  :rows="10" :readonly="true" v-model="to.text">
        </el-input>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="24">
        <el-cascader
          placeholder="试试搜索"
          :options="from.option"
          :show-all-levels="false"
          :filterable="true"
          expand-trigger="hover"
          v-model="from.language"
        ></el-cascader>
        <el-button type="text" :disabled="exchangeDisabled" @click.stop="exchangeLanguage">
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
    <el-button style="margin: 1rem" type="primary" :disabled="!hasText" @click.stop="translate" :loading="translating">
      翻译
    </el-button>
  </div>
</template>

<script>
  import _ from 'lodash'
  import http from '../tools/http'
  import qs from 'qs'

  import options, { language } from '../tools/languages'

  const fromOptions = _.cloneDeep(options)
  const toOptions = _.cloneDeep(options)
  toOptions.shift()

  let findLanguage = (key, value = [], opts = options) => {
    for (let option of opts) {
      if (key === option.value) {
        // 找到了
        value.push(key)
        return value
      }

      if (option.children) {
        let _value = _.cloneDeep(value)
        _value.push(option.value)
        _value = findLanguage(key, _value, option.children)
        if (_value) {
          // 在children中找到了
          return _value
        }
      }
    }

    return null
  }

  export default {
    name: 'TextTranslator',
    data () {
      return {
        from: {
          option: fromOptions,
          language: ['auto'],
          text: '',
          detectedLanguage: ''
        },
        to: {
          option: toOptions,
          language: ['zh'],
          text: ''
        },
        translating: false
      }
    },
    computed: {
      hasText () {
        return !_.isEmpty(this.from.text.trim())
      },
      detectedLanguageName () {
        return language.get(this.from.detectedLanguage)
      },
      exchangeDisabled () {
        let from = _.cloneDeep(this.from.language).pop()
        return from === 'auto' && _.isEmpty(this.from.detectedLanguage)
      }
    },
    methods: {
      exchangeLanguage () {
        let _from = _.cloneDeep(this.from.language).pop()
        let _to = _.cloneDeep(this.to.language).pop()
        if (_from === 'auto') {
          _from = this.from.detectedLanguage
        }
        if (_.isEmpty(_from) || _.isEmpty(_to)) {
          return
        }

        this.from.language = findLanguage(_to)
        this.to.language = findLanguage(_from)
        this.from.detectedLanguage = ''

        this.translate()
      },
      translate () {
        let q = this.from.text.trim()
        if (_.isEmpty(q)) {
          return
        }

        let from = _.cloneDeep(this.from.language).pop()
        let to = _.cloneDeep(this.to.language).pop()

        if (from === to) {
          this.to.text = this.from.text
          return
        }

        this.translating = true
        http({
          url: 'trans/text',
          method: 'post',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          },
          data: qs.stringify({q, from, to})
        }).then(({data}) => {
          this.to.text = data.trans_result.map((result) => result.dst).join('\n')
          if (from === 'auto') {
            this.from.detectedLanguage = data.from
          } else {
            this.from.detectedLanguage = ''
          }
        }).finally(() => {
          this.translating = false
        })
      },
      clear () {
        this.from.text = ''
        this.from.detectedLanguage = ''
        this.to.text = ''
      }
    }
  }
</script>

<style scoped lang="scss">
  .detected-language {
    position: absolute;
    bottom: 5px;
    right: 20px;
    background-color: #B4BCCC;
    color: white;
    padding: 5px;
    border-radius: 5px;
  }
</style>

