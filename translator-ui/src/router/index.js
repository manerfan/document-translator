import Vue from 'vue'
import Router from 'vue-router'
import Layout from '@/views/Layout'
import Home from '@/components/Home'
import TextTranslator from '@/components/TextTranslator'
import DocTranslator from '@/components/DocTranslator'
import SentenceBreaker from '@/components/SentenceBreaker'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Layout',
      component: Layout,
      children: [
        {
          path: '/',
          name: 'Home',
          component: Home
        },
        {
          path: '/trans/text',
          name: 'Text Translator',
          component: TextTranslator
        },
        {
          path: '/trans/sbd',
          name: 'Sentence Breaker',
          component: SentenceBreaker
        },
        {
          path: '/trans/doc',
          name: 'Doc Translator',
          component: DocTranslator
        }
      ]
    }
  ]
})
