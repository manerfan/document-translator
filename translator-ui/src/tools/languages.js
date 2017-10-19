/*
 * ManerFan(http://manerfan.com). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

const language = new Map()
language.set('auto', '自动检测')
language.set('ara', '阿拉伯语')
language.set('est', '爱沙尼亚语')
language.set('bul', '保加利亚语')
language.set('pl', '波兰语')
language.set('dan', '丹麦语')
language.set('de', '德语')
language.set('ru', '俄语')
language.set('fra', '法语')
language.set('fin', '芬兰语')
language.set('kor', '韩语')
language.set('nl', '荷兰语')
language.set('cs', '捷克语')
language.set('rom', '罗马尼亚语')
language.set('pt', '葡萄牙语')
language.set('jp', '日语')
language.set('swe', '瑞典语')
language.set('slo', '斯洛文尼亚语')
language.set('th', '泰语')
language.set('wyw', '文言文')
language.set('spa', '西班牙语')
language.set('el', '希腊语')
language.set('hu', '匈牙利语')
language.set('zh', '中文')
language.set('en', '英语')
language.set('it', '意大利语')
language.set('vie', '越南语')
language.set('yue', '粤语')
language.set('cht', '中文繁体')

export { language }

export default [
  {value: 'auto', label: language.get('auto')},
  {value: 'zh', label: language.get('zh')},
  {value: 'en', label: language.get('en')},
  {
    value: 'abc',
    label: 'ABC',
    children: [
      {value: 'ara', label: language.get('ara')},
      {value: 'est', label: language.get('est')},
      {value: 'bul', label: language.get('bul')},
      {value: 'pl', label: language.get('pl')}
    ]
  },
  {
    value: 'defg',
    label: 'DEFG',
    children: [
      {value: 'dan', label: language.get('dan')},
      {value: 'de', label: language.get('de')},
      {value: 'ru', label: language.get('ru')},
      {value: 'fra', label: language.get('fra')},
      {value: 'fin', label: language.get('fin')}
    ]
  },
  {
    value: 'hijklmn',
    label: 'HIJKLMN',
    children: [
      {value: 'kor', label: language.get('kor')},
      {value: 'nl', label: language.get('nl')},
      {value: 'cs', label: language.get('cs')},
      {value: 'rom', label: language.get('rom')}
    ]
  },
  {
    value: 'opqrst',
    label: 'OPQRST',
    children: [
      {value: 'pt', label: language.get('pt')},
      {value: 'jp', label: language.get('jp')},
      {value: 'swe', label: language.get('swe')},
      {value: 'slo', label: language.get('slo')},
      {value: 'th', label: language.get('th')}
    ]
  },
  {
    value: 'uvwx',
    label: 'UVWX',
    children: [
      {value: 'wyw', label: language.get('wyw')},
      {value: 'spa', label: language.get('spa')},
      {value: 'el', label: language.get('el')},
      {value: 'hu', label: language.get('hu')}
    ]
  },
  {
    value: 'yz',
    label: 'YZ',
    children: [
      {value: 'zh', label: language.get('zh')},
      {value: 'en', label: language.get('en')},
      {value: 'it', label: language.get('it')},
      {value: 'vie', label: language.get('vie')},
      {value: 'yue', label: language.get('yue')},
      {value: 'cht', label: language.get('cht')}
    ]
  }
]
