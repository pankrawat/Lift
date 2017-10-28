package com.liftindia.app.bean;

import java.util.List;

/**
 * Created by appsquadz on 20/9/16.
 */
public class VehicleTypeBean {

    /**
     * isSuccess : true
     * message : Result return successfully
     * Result : [{"typeId":"1","type":"4-Wheeler","brands":[{"brandId":"1","brandName":"Chevrolet","typeId":"1","models":[{"modelId":"1","name":"Spark","brandId":"1"},{"modelId":"2","name":"UVA","brandId":"1"}]},{"brandId":"2","brandName":"Fiat","typeId":"1","models":[{"modelId":"3","name":"Palio stile","brandId":"2"}]},{"brandId":"3","brandName":"Hyundai","typeId":"1","models":[{"modelId":"4","name":"Getz Prime","brandId":"3"},{"modelId":"5","name":"Santro Xing","brandId":"3"},{"modelId":"6","name":"i10","brandId":"3"}]},{"brandId":"4","brandName":"Maruti","typeId":"1","models":[{"modelId":"7","name":"Alto","brandId":"4"},{"modelId":"8","name":"800","brandId":"4"},{"modelId":"9","name":"WagonR","brandId":"4"},{"modelId":"10","name":"Zen Estilo","brandId":"4"},{"modelId":"11","name":"Swift","brandId":"4"}]},{"brandId":"5","brandName":"Skoda","typeId":"1","models":[{"modelId":"12","name":"Fabia","brandId":"5"}]},{"brandId":"6","brandName":"Tata","typeId":"1","models":[{"modelId":"13","name":"Indica","brandId":"6"}]},{"brandId":"7","brandName":"Tata","typeId":"1","models":[{"modelId":"14","name":"Aveo","brandId":"7"},{"modelId":"15","name":"SRV","brandId":"7"}]},{"brandId":"8","brandName":"Ford","typeId":"1","models":[{"modelId":"16","name":"Fiesta","brandId":"8"},{"modelId":"17","name":"Ikon","brandId":"8"}]},{"brandId":"9","brandName":"Hindustan","typeId":"1","models":[{"modelId":"18","name":"Ambassador","brandId":"9"}]},{"brandId":"11","brandName":"Honda","typeId":"1","models":[]},{"brandId":"12","brandName":"Mahindra","typeId":"1","models":[{"modelId":"20","name":"Logan","brandId":"12"}]},{"brandId":"14","brandName":"Mitsubishi","typeId":"1","models":[{"modelId":"23","name":"Lancer","brandId":"14"}]},{"brandId":"15","brandName":"Tata","typeId":"1","models":[{"modelId":"24","name":"Indigo CS","brandId":"15"},{"modelId":"25","name":"Indigo","brandId":"15"}]},{"brandId":"20","brandName":"Hero","typeId":"1","models":[{"modelId":"30","name":"Octavia","brandId":"20"}]},{"brandId":"21","brandName":"Toyota","typeId":"1","models":[{"modelId":"31","name":"Corolla","brandId":"21"}]},{"brandId":"24","brandName":"Toyota","typeId":"1","models":[{"modelId":"34","name":"Camry","brandId":"24"}]},{"brandId":"26","brandName":"Audi","typeId":"1","models":[{"modelId":"36","name":"4","brandId":"26"}]},{"brandId":"27","brandName":"BMW 3","typeId":"1","models":[{"modelId":"37","name":"3 series","brandId":"27"}]},{"brandId":"28","brandName":"VW ","typeId":"1","models":[{"modelId":"38","name":"Passat","brandId":"28"}]},{"brandId":"32","brandName":"Tata","typeId":"1","models":[{"modelId":"42","name":"Sumo Vita","brandId":"32"},{"modelId":"43","name":"Sumo Grande","brandId":"32"}]},{"brandId":"33","brandName":"Toyota","typeId":"1","models":[{"modelId":"44","name":"Innova","brandId":"33"}]},{"brandId":"35","brandName":"Maini","typeId":"1","models":[{"modelId":"46","name":"Reva","brandId":"35"}]},{"brandId":"45","brandName":"Nissan","typeId":"1","models":[{"modelId":"59","name":"Xtrail","brandId":"45"}]},{"brandId":"46","brandName":"Suzuki","typeId":"1","models":[{"modelId":"60","name":"Grand Vitara","brandId":"46"}]},{"brandId":"47","brandName":"Honda","typeId":"1","models":[{"modelId":"61","name":"Safari dicor","brandId":"47"}]}]},{"typeId":"2","type":"3-Wheeler","brands":[{"brandId":"19","brandName":"Bajaj","typeId":"2","models":[{"modelId":"29","name":"Cedia","brandId":"19"}]},{"brandId":"29","brandName":"Piaggio","typeId":"2","models":[{"modelId":"39","name":"Teana","brandId":"29"}]}]},{"typeId":"3","type":"2-Wheeler","brands":[{"brandId":"25","brandName":"Bajaj","typeId":"3","models":[{"modelId":"35","name":"Laura/ Superb","brandId":"25"}]}]}]
     */

    private boolean isSuccess;
    private String message;
    /**
     * typeId : 1
     * type : 4-Wheeler
     * brands : [{"brandId":"1","brandName":"Chevrolet","typeId":"1","models":[{"modelId":"1","name":"Spark","brandId":"1"},{"modelId":"2","name":"UVA","brandId":"1"}]},{"brandId":"2","brandName":"Fiat","typeId":"1","models":[{"modelId":"3","name":"Palio stile","brandId":"2"}]},{"brandId":"3","brandName":"Hyundai","typeId":"1","models":[{"modelId":"4","name":"Getz Prime","brandId":"3"},{"modelId":"5","name":"Santro Xing","brandId":"3"},{"modelId":"6","name":"i10","brandId":"3"}]},{"brandId":"4","brandName":"Maruti","typeId":"1","models":[{"modelId":"7","name":"Alto","brandId":"4"},{"modelId":"8","name":"800","brandId":"4"},{"modelId":"9","name":"WagonR","brandId":"4"},{"modelId":"10","name":"Zen Estilo","brandId":"4"},{"modelId":"11","name":"Swift","brandId":"4"}]},{"brandId":"5","brandName":"Skoda","typeId":"1","models":[{"modelId":"12","name":"Fabia","brandId":"5"}]},{"brandId":"6","brandName":"Tata","typeId":"1","models":[{"modelId":"13","name":"Indica","brandId":"6"}]},{"brandId":"7","brandName":"Tata","typeId":"1","models":[{"modelId":"14","name":"Aveo","brandId":"7"},{"modelId":"15","name":"SRV","brandId":"7"}]},{"brandId":"8","brandName":"Ford","typeId":"1","models":[{"modelId":"16","name":"Fiesta","brandId":"8"},{"modelId":"17","name":"Ikon","brandId":"8"}]},{"brandId":"9","brandName":"Hindustan","typeId":"1","models":[{"modelId":"18","name":"Ambassador","brandId":"9"}]},{"brandId":"11","brandName":"Honda","typeId":"1","models":[]},{"brandId":"12","brandName":"Mahindra","typeId":"1","models":[{"modelId":"20","name":"Logan","brandId":"12"}]},{"brandId":"14","brandName":"Mitsubishi","typeId":"1","models":[{"modelId":"23","name":"Lancer","brandId":"14"}]},{"brandId":"15","brandName":"Tata","typeId":"1","models":[{"modelId":"24","name":"Indigo CS","brandId":"15"},{"modelId":"25","name":"Indigo","brandId":"15"}]},{"brandId":"20","brandName":"Hero","typeId":"1","models":[{"modelId":"30","name":"Octavia","brandId":"20"}]},{"brandId":"21","brandName":"Toyota","typeId":"1","models":[{"modelId":"31","name":"Corolla","brandId":"21"}]},{"brandId":"24","brandName":"Toyota","typeId":"1","models":[{"modelId":"34","name":"Camry","brandId":"24"}]},{"brandId":"26","brandName":"Audi","typeId":"1","models":[{"modelId":"36","name":"4","brandId":"26"}]},{"brandId":"27","brandName":"BMW 3","typeId":"1","models":[{"modelId":"37","name":"3 series","brandId":"27"}]},{"brandId":"28","brandName":"VW ","typeId":"1","models":[{"modelId":"38","name":"Passat","brandId":"28"}]},{"brandId":"32","brandName":"Tata","typeId":"1","models":[{"modelId":"42","name":"Sumo Vita","brandId":"32"},{"modelId":"43","name":"Sumo Grande","brandId":"32"}]},{"brandId":"33","brandName":"Toyota","typeId":"1","models":[{"modelId":"44","name":"Innova","brandId":"33"}]},{"brandId":"35","brandName":"Maini","typeId":"1","models":[{"modelId":"46","name":"Reva","brandId":"35"}]},{"brandId":"45","brandName":"Nissan","typeId":"1","models":[{"modelId":"59","name":"Xtrail","brandId":"45"}]},{"brandId":"46","brandName":"Suzuki","typeId":"1","models":[{"modelId":"60","name":"Grand Vitara","brandId":"46"}]},{"brandId":"47","brandName":"Honda","typeId":"1","models":[{"modelId":"61","name":"Safari dicor","brandId":"47"}]}]
     */

    private List<ResultBean> Result;

    public boolean isIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ResultBean> getResult() {
        return Result;
    }

    public void setResult(List<ResultBean> Result) {
        this.Result = Result;
    }

    public static class ResultBean {
        private String typeId;
        private String type;
        /**
         * brandId : 1
         * brandName : Chevrolet
         * typeId : 1
         * models : [{"modelId":"1","name":"Spark","brandId":"1"},{"modelId":"2","name":"UVA","brandId":"1"}]
         */

        private List<BrandsBean> brands;

        public String getTypeId() {
            return typeId;
        }

        public void setTypeId(String typeId) {
            this.typeId = typeId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<BrandsBean> getBrands() {
            return brands;
        }

        public void setBrands(List<BrandsBean> brands) {
            this.brands = brands;
        }

        public static class BrandsBean {
            private String brandId;
            private String brandName;
            private String typeId;
            /**
             * modelId : 1
             * name : Spark
             * brandId : 1
             */

            private List<ModelsBean> models;

            public String getBrandId() {
                return brandId;
            }

            public void setBrandId(String brandId) {
                this.brandId = brandId;
            }

            public String getBrandName() {
                return brandName;
            }

            public void setBrandName(String brandName) {
                this.brandName = brandName;
            }

            public String getTypeId() {
                return typeId;
            }

            public void setTypeId(String typeId) {
                this.typeId = typeId;
            }

            public List<ModelsBean> getModels() {
                return models;
            }

            public void setModels(List<ModelsBean> models) {
                this.models = models;
            }

            public static class ModelsBean {
                private String modelId;
                private String name;
                private String brandId;

                public String getModelId() {
                    return modelId;
                }

                public void setModelId(String modelId) {
                    this.modelId = modelId;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getBrandId() {
                    return brandId;
                }

                public void setBrandId(String brandId) {
                    this.brandId = brandId;
                }
            }
        }
    }
}
