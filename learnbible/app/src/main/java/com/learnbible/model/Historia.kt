package com.learnbible.model

import java.io.Serializable

class Historia(var id: String,
               var titulo: String,
               var avanceHistoria: Int,
               var cantVersiculos: Int,
               var listVericulos: List<Versiculo>
) : Serializable