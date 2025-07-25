import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
  timeout: 5000,
  withCredentials: true,
})

api.interceptors.request.use(
  (config) => {
    console.log('%c[API Request]', 'color: green; font-weight: bold', {
      url: config.url,
      method: config.method,
      params: config.params,
      data: config.data,
      headers: config.headers,
    })

    return config
  },
  (error) => {
    console.error('[API Request Error]', error)
    return Promise.reject(error)
  },
)

api.interceptors.response.use(
  (response) => {
    console.log('%c[API Response]', 'color: green; font-weight: bold', {
      url: response.config.url,
      method: response.config.method,
      status: response.status,
      data: response.data,
    })

    return response
  },
  (error) => {
    if (error.response) {
      console.error('%c[API Response Error]', 'color: red; font-weight: bold', {
        url: error.config.url,
        method: error.config.method,
        status: error.response.status,
        data: error.response.data,
      })
    } else {
      console.error('%c[API Network Error]', 'color: red; font-weight: bold', error)
    }

    return Promise.reject(error)
  },
)

export default api
