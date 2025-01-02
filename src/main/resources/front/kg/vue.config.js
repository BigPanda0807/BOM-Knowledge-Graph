module.exports = {
  lintOnSave: false,
  transpileDependencies: true,
  devServer: {
    host: "localhost",
    hot: true,
    port: 3000,
    compress: true,
    proxy: { //配置跨域
      '/api': {
        target: 'http://localhost:8080/',
        secure: false,
        changOrigin: true,
        pathRewrite: {
          '^/api': ''
        }
      }
    },
    client: {
      overlay: false,
    },
  }
}
