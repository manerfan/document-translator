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

import axios from 'axios'
import _ from 'lodash'
import Fingerprintjs2 from 'fingerprintjs2'
import { Message } from 'element-ui'

function getDeviceId () {
  return new Promise(function (resolve, reject) {
    new Fingerprintjs2().get(function (result, components) {
      resolve(result)
    })
  })
}

axios.defaults.baseURL = '/api'
axios.interceptors.request.use(
  config => {
    return getDeviceId().then((deviceId) => {
      config.headers = _.assign(config.headers, {
        'x-client-type': 'web',
        'version': 'v1.0.0',
        'x-client-id': deviceId
      })
      return config
    })
  },
  err => {
    Message({
      message: '哎呀，程序开了个小差！',
      type: 'error'
    })
    console.log(err)
    return Promise.reject(err)
  }
)

axios.interceptors.response.use(
  response => {
    if (response.status >= 200 && response.status < 300) {
      return response.data
    }
    return Promise.reject(response)
  },
  error => {
    return Promise.reject(error)
  }
)

export default axios
