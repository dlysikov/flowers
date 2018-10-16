export const environment = {
  production: true,
  apiEndpoint: (function() {
      return document.location.protocol + "//" + "api-" + document.location.hostname + (document.location.port ? ":" + document.location.port : "");
  })(),
  translationPrefix: "/resources/assets/i18n/",
  imagesLocation:  "/resources/assets/img/",
  pagesLocation: {
    gtc: {
      en: "/resources/assets/pages/gtc_en.html",
      fr:"/resources/assets/pages/gtc_fr.html"
    }
  }
};
