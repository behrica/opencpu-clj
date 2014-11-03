(ns opencpu-clj.utils)


(defn- param-to-multipart [[ key value :as key-value]]

  (if (:file value)
    {:name (name key) :content (clojure.java.io/file (:file value))  }
    {:name (name key) :content value} ))

(defn params-map
  "Converts input params of object function into clj-hhtp compliant map,
  taking care of multipart in case of file uploads"
  [params]
  (if (every? nil?  (map :file  (vals params)))
    {:form-params params}
    {:multipart (vec  (map param-to-multipart params))}))
