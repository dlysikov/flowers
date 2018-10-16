export const environment = {
  production: false,
  apiEndpoint: (function() {
    return 'https://localhost:8234/api'
  })(),
  translationPrefix: "/assets/i18n/",
  imagesLocation: "/assets/img/",
  pagesLocation: {
    gtc: {
      en:"/assets/pages/gtc_en.html",
      fr:"/assets/pages/gtc_fr.html"
    }
  }
};
