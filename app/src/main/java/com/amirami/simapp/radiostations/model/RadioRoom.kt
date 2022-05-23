package com.amirami.simapp.radiostations.model

data class RadioRoom (

    var  radiouid:String="",
    var   name:String="",
    var  bitrate:String="",
    var  homepage: String="",
    var  favicon: String="",
    var  tags: String="",
    var  country: String="",
    var  state: String="",
      //var RadiostateDB: String?,
    var   language: String="",
    var  streamurl: String="",
    var   fav: Boolean=true,
)
{
  /*  var radiouid:String=""
    var name: String=""
    var bitrate: String=""
    var homepage: String=""
    var favicon: String=""
    var tags: String=""
    var country: String=""
    var state: String=""
    var language: String=""
    var streamurl: String=""
*/


    init {
        this.radiouid = radiouid!!
        this.name = name!!
        this.bitrate = bitrate!!
        this.homepage = homepage!!
        this.favicon = favicon!!
        this.tags = tags!!
        this.country = country!!
        this.state = state!!
        this.language = language!!
        this.streamurl = streamurl!!
    }
}