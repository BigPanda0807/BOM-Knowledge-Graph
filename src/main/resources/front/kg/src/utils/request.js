import axios from 'axios'
import { Message } from 'element-ui';
import router from '@/router/index'

/**
* 封装get方法
* @param url
* @param data
* @returns {Promise}
*/

export function get(url, params = {}) {
    return new Promise((resolve, reject) => {
        axios.get(url, {
            params: params
        })
            .then(response => {
                resolve(response.data)
            })
            .catch(err => {
                reject(err)
            })
    })
}

/**
* 封装post请求 
* @param url
* @param data 走json结构
* @returns {Promise}
*/

export function post(url, data = {}) {
    return new Promise((resolve, reject) => {
        axios.post(url, data)
            .then(response => {
                resolve(response.data)
            }, err => {
                reject(err)
            })
    })
}
