(set-env!
 :dependencies '[;; boot
                 [adzerk/boot-cljs "0.0-2814-3" :scope "test"]
                 [adzerk/boot-cljs-repl "0.1.9" :scope "test"]
                 [adzerk/boot-reload "0.2.6" :scope "test"]
                 [pandeiro/boot-http "0.6.3-SNAPSHOT" :scope "test"]

                 ;; cljs
                 [org.clojure/clojurescript "0.0-3126"]
                 [reagent "0.5.0"]
                 ]
 :source-paths #{"src/clj" "src/cljs"}
 :resource-paths #{"resources"}
 :target-path "target")

(require
 '[adzerk.boot-cljs :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload :refer [reload]]
 '[pandeiro.boot-http :refer [serve]])

(deftask dev
  []
  (comp
   (serve :dir "target/public"
          :httpkit true)
   (watch)
   (speak)
   (reload)
   (cljs-repl)
   (cljs :output-to "public/js/main.js"
         :optimizations :none
         :source-maps true)))
