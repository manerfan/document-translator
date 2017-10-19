<template>
  <div class="home">
    <AppHeader active="/trans/sbd"></AppHeader>
    <h2>现代中英文断句</h2>
    <h4>利用标点符号进行断句。</h4>
    <el-row :gutter="20" style="margin: 2rem 2rem">
      <el-col class="input-col" :span="12">
        <el-input type="textarea" placeholder="请输入要断句的文字" size="large" resize="none"
                  :rows="10" :maxlength="2000" v-model="from">
        </el-input>
        <el-button class="btn-close" type="text" icon="el-icon-error" v-if="hasText" @click.stop="clear"></el-button>
      </el-col>
      <el-col :span="12">
        <el-input type="textarea" size="large" resize="none"
                  :rows="10" :readonly="true" v-model="to">
        </el-input>
      </el-col>
    </el-row>
    <el-button type="success" :disabled="!hasText" :loading="breaking" @click.stop="sentbreak"> 断句 </el-button>
  </div>
</template>

<script>
  import AppHeader from './Header.vue'
  import _ from 'lodash'
  import http from '../tools/http'
  import qs from 'qs'

  export default {
    components: {
      AppHeader
    },
    name: 'SentenceBreaker',
    data () {
      return {
        from: '',
        to: '',
        breaking: false
      }
    },
    computed: {
      hasText () {
        return !_.isEmpty(this.from.trim())
      }
    },
    methods: {
      clear () {
        this.from = ''
        this.to = ''
      },
      sentbreak () {
        let q = this.from.trim()
        if (_.isEmpty(q)) {
          return
        }

        this.breaking = true
        http({
          url: 'trans/sbd',
          method: 'post',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          },
          data: qs.stringify({q})
        }).then(({data}) => {
          let idx = 1
          this.to = data.map((d) => `${idx++}. ${d}`).join('\n')
        }).finally(() => {
          this.breaking = false
        })
      }
    }
  }
</script>
