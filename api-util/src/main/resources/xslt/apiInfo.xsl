<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/TR/xhtml1/strict">
    <xsl:output method="html" version="4.0" encoding="utf-8" indent="yes"/>

    <xsl:template match="/">
        <html lang="zh" xml:lang="zh" xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <meta charset="UTF-8"/>
                <script type="text/javascript" src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
                <style type="text/css">
                    table, td, th {
                    margin: 5px;
                    padding: 5px;
                    }

                    .hide, .detail {
                    display: none;
                    }

                    .title {
                    text-align: left;
                    display: block;
                    font-weight: bold;
                    }

                    .title p {
                    font-size: 32px;
                    font-style: normal;
                    font-weight: bold;
                    text-align: center
                    }

                    .div_resp {
                    border: solid 1px #D4E2F1;
                    padding: 5px;
                    margin: 5px;
                    }

                    .div_resp table {
                    margin: 0px;
                    padding: 0px
                    }

                    .div_resp td, .div_resp th {
                    padding: 2px 10px 2px 5px;
                    vertical-align: super;
                    }

                    .tbl_err_code table {
                    border-right: 1px solid #D4E2F1;
                    border-bottom: 1px solid #D4E2F1;
                    border-spacing: 0px;
                    padding: 0px;
                    width: 980px;
                    }

                    .tbl_err_code table td, .tbl_err_code table th {
                    border-left: 1px solid #D4E2F1;
                    border-top: 1px solid #D4E2F1
                    }

                    .div_detail_resp {
                    border: solid 1px #D4E2F1;
                    padding: 5px;
                    margin: 5px;
                    }

                    .div_detail_resp table {
                    margin: 0px;
                    padding: 0px
                    }

                    .div_detail_resp td, .div_detail_resp th {
                    padding: 2px 10px 2px 5px;
                    vertical-align: super;
                    }

                    .div_left_groups {
                    width: 150px;
                    float: left;
                    display: block;
                    }

                    .div_main_content {
                    width: 810px;
                    float: right;
                    }

                    .div_comm_err, .div_comm_return, .div_comm_sys_param {
                    float: left;
                    width: inherit;
                    padding: 2px;
                    padding-bottom: 8px;
                    margin-bottom: 10px;
                    border-bottom: solid 1px #D4E2F1;
                    }

                    .main {
                    width: 990px;
                    height: inherit;
                    min-height: 558px;
                    margin: 0 auto;
                    }

                    ul {
                    list-style: none;
                    padding: 0px;
                    margin: 0px;
                    border-bottom: 1px solid #D4E2F1;
                    border-left: 1px solid #D4E2F1;
                    }

                    ul li {
                    display: list-item;
                    text-decoration: none;
                    text-align: left;
                    padding: 3px 3px 3px 5px;
                    font-size: 16px;
                    font-style: normal;
                    font-variant: normal;
                    font-weight: normal;
                    border-top: 1px solid #D4E2F1;
                    border-right: 1px solid #D4E2F1;
                    cursor: pointer;
                    }

                    ul li:hover {
                    background-color: #D4E2F1;
                    text-align: left;
                    padding: 3px 3px 3px 5px;
                    }

                    ul li.visited {
                    background-color: #D4E2F1;
                    text-align: left;
                    padding: 3px 3px 3px 5px;
                    font-weight: bold;
                    }

                    .div_detail_toggle {
                    margin: 5px 1px 0px 1px;
                    cursor: pointer;
                    padding: 0px 2px 0px 2px;
                    }

                    .div_detail_toggle span.api_name {
                    font-size: 17px;
                    width: 370px;
                    text-align: left;
                    display: inline-block;
                    }

                    .div_detail_toggle span.api_name:hover {
                    font-size: 17px;
                    width: 370px;
                    text-align: left;
                    display: inline-block;
                    color: #f60;
                    }

                    .div_detail_toggle span.api_desc {
                    font-size: 17px;
                    }

                    .div_detail_toggle:hover {
                    background-color: #D4E2F1;
                    }

                    .div_detail_toggle.detail_visited {
                    background-color: #D4E2F1;
                    font-weight: bold;
                    }

                    .detail {
                    padding: 1px 0px 0px 10px;
                    margin: 0px 1px 1px 1px;
                    border-bottom: solid 1px #D4E2F1;
                    }

                    .detail.detail_visited {
                    background-color: #FFFFFF;
                    }

                    .top {
                    float: left;
                    width: inherit;
                    padding: 0px 0px 10px 0px;
                    margin: 0px 0px 10px 0px;
                    border-bottom: solid 1px #D4E2F1;
                    }

                    .tbl_border_collapse {
                    border: solid 1px #D4E2F1;
                    border-collapse: collapse;
                    padding: 5px;
                    margin: 5px;
                    width: 788px;
                    }

                    .tbl_border_collapse td, .tbl_border_collapse th {
                    border: solid 1px #D4E2F1;
                    }

                    .div_busi_err {
                    padding-bottom: 10px;
                    }

                    .tbl_title_tr {
                    background-color: rgb(216, 229, 244);
                    }

                    .tbl_even_backgroud {
                    background-color: rgb(255, 255, 255);
                    }

                    .tbl_odd_backgroud {
                    background-color: rgb(241, 247, 251);
                    }

                    .div_detail_resp table.tbl_no_border_collapse {
                    width: 776px;
                    }

                    .tbl_no_border_collapse {
                    width: 970px;
                    border-spacing: 0px;
                    margin-top: 3px;
                    }

                    .tbl_no_border_collapse tr.tbl_even_backgroud {
                    background-color: rgb(255, 255, 255);
                    }

                    .tbl_no_border_collapse tr.tbl_odd_backgroud {
                    background-color: rgb(241, 247, 251);
                    border-right: solid 1px #D4E2F1;
                    }

                    .tbl_no_border_collapse tr.tbl_odd_backgroud td {
                    background-color: rgb(241, 247, 251);
                    }

                    .highlight {
                    background: yellow;
                    color: red;
                    }

                    #search_box {
                    background: white;
                    opacity: 0.8;
                    text-align: right
                    }

                    #search_btn {
                    background: #0f79be;
                    border: 0px;
                    margin-top: 10px;
                    width: 120px;
                    line-height: 26px;
                    height: 28px;
                    color: white;
                    }

                    #searchstr {
                    font-size: 14px;
                    height: 24px;
                    width: 180px;
                    margin-top: 10px;
                    }

                    .underline {
                    border-bottom: solid 1px #D4E2F1;
                    padding-bottom: 4px;
                    margin-bottom: 2px;
                    }

                    #internal_apis, #integrated_apis, #none_apis, #registereddevice_apis, #userlogin_apis, #other_apis, #doc_apis {
                    margin: 0px 0px 5px 0px;
                    padding: 0px;
                    border: solid 1px #D4E2F1;
                    }

                    #doc_apis span.state_title, #internal_apis span.state_title, #integrated_apis span.state_title, #none_apis span.state_title,
                    #registereddevice_apis span.state_title, #userlogin_apis span.state_title, #other_apis span.state_title {
                    font-weight: bold;
                    }

                </style>
                <script type="text/javascript">
                    <![CDATA[
                    //js 获取api的groupNames
                    $(document).ready(function() {
                      var groups = new Array();
                      var last_group = "";
                      $(".i18n").each(function(index) {
                        var source = '\nzh-cn:' + $(this).text();
                        var lo = 'zh-cn';
                        if (location.search) {
                          var index = location.search.indexOf('lo=');
                          if (index > 0) {
                            if (location.search.length >= index + 8) {
                              lo = location.search.substr(index + 3, 5);
                            }
                          }
                        }

                        var regExp = new RegExp('\\n\\s{0,4}' + lo + ':(.+)', 'i');
                        if (regExp.test(source)) {
                          var content = regExp.exec(source)[1].split('\n');
                          var buffer = "";
                          for (var i = 0; content.length > i; i++) {
                            var line = content[i].trim();
                            if (line.length > 6 && line.charAt(2) == '-' && line.charAt(5) == ':') {
                              break;
                            }
                            buffer += line;
                          }

                          $(this).text(buffer);
                        }
                      });
                      $("a.hide").each(function(index) {
                        var current_group = $(this).text();
                        if (last_group != current_group) {
                          groups.push(current_group);
                          last_group = current_group;
                        }
                      });
                      left_nav_group_apis(groups);

                      //table 斑马线效果：
                      $("table.tbl_border_collapse tr:nth-child(2n+3)").addClass("tbl_even_backgroud");
                      $("table.tbl_border_collapse tr:nth-child(2n)").addClass("tbl_odd_backgroud");
                      $("table.tbl_border_collapse tr:nth-child(1)").addClass("tbl_title_tr");
                      $("table.tbl_no_border_collapse tr:nth-child(2n)").addClass("tbl_even_backgroud");
                      $("table.tbl_no_border_collapse tr:nth-child(2n+1)").addClass("tbl_odd_backgroud");

                      $("#search_box").fixDiv({
                        top: 0
                      });

                      i = 0; //全局变量
                      sCurText = $('#searchstr').val(); //获取你输入的关键字
                      $('#search_btn').click(function() {
                        highlight();
                      }); //点击search时，执行highlight函数；
                      $('#searchstr').keyup(function(e) {
                        highlight();
                      });

                      var url = window.location.href; //获取URL地址
                      if (url.indexOf("#") != -1) {
                        var str = url.substr(url.indexOf("#") + 1);
                        $("input#searchstr").val(str);
                        highlight(str);
                      }
                    });

                    /**
                     *生成左部 api 目录
                     *添加click 方法
                     */

                    function left_nav_group_apis(groups) {
                      $("div#groups ul").empty();
                      $.each(groups, function(n, value) {
                        $("div#groups ul").append("<li>" + value + "</li>");
                        $("div#groups ul li:last").click(function() {
                          show_curr_group_apis($(this).text());
                          li_visited_class(this);
                        });
                      });

                      var first_group = $("div#groups ul li:first").text();
                      $("div#groups ul li:first").click();
                    }


                    /**
                     *隐藏其他groups
                     *保留当前group的apis可见
                     */
                    function show_curr_group_apis(group_name) {
                      $("div.api").hide();
                      $("div [id='other_apis']").empty();
                      $("div [id='internal_apis']").empty();
                      $("div [id='integrated_apis']").empty();
                      $("div [id='none_apis']").empty();
                      $("div [id='registereddevice_apis']").empty();
                      $("div [id='userlogin_apis']").empty();
                      $("div [id='doc_apis']").empty();


                      $("div [id='none_apis']").append("<span class='state_title'>NONE</span>");
                      $("div [id='registereddevice_apis']").append("<span class='state_title'>REGISTERED_DEVICE</span>");
                      $("div [id='userlogin_apis']").append("<span class='state_title'>USER_LOGIN</span>");
                      $("div [id='internal_apis']").append("<span class='state_title'>INTERNAL</span>");
                      $("div [id='integrated_apis']").append("<span class='state_title'>INTEGRATED</span>");
                      $("div [id='other_apis']").append("<span class='state_title'>OTHER</span>");
                      $("div [id='doc_apis']").append("<span class='state_title'>DOCUMENT</span>");


                      $("div [groupname='" + group_name + "']").each(function(index) {
                        if ("Internal" == $(this).find("div[id='api_securityLevel'] span").text()) {
                          $("div [id='internal_apis']").append("<div class='api' groupname='" + group_name + "' ></div>");
                          $("div [id='internal_apis'] div.api:last").append($(this).html());
                        } else if ("Integrated" == $(this).find("div[id='api_securityLevel'] span").text()) {
                          $("div [id='integrated_apis']").append("<div class='api' groupname='" + group_name + "' ></div>");
                          $("div [id='integrated_apis'] div.api:last").append($(this).html());
                        } else if ("None" == $(this).find("div[id='api_securityLevel'] span").text()) {
                          $("div [id='none_apis']").append("<div class='api' groupname='" + group_name + "' ></div>");
                          $("div [id='none_apis'] div.api:last").append($(this).html());
                        } else if ("RegisteredDevice" == $(this).find("div[id='api_securityLevel'] span").text()) {
                          $("div [id='registereddevice_apis']").append("<div class='api' groupname='" + group_name + "' ></div>");
                          $("div [id='registereddevice_apis'] div.api:last").append($(this).html());
                        } else if ("UserLogin" == $(this).find("div[id='api_securityLevel'] span").text()) {
                          $("div [id='userlogin_apis']").append("<div class='api' groupname='" + group_name + "' ></div>");
                          $("div [id='userlogin_apis'] div.api:last").append($(this).html());
                        } else if ("Document" == $(this).find("div[id='api_securityLevel'] span").text()) {
                          $("div [id='doc_apis']").append("<div class='api' groupname='" + group_name + "' ></div>");
                          $("div [id='doc_apis'] div.api:last").append($(this).html());
                        } else {
                          $("div [id='other_apis']").append("<div class='api' groupname='" + group_name + "' ></div>");
                          $("div [id='other_apis'] div.api:last").append($(this).html());
                        }
                      });
                      if ($("div [id='other_apis'] div").length > 0) {
                        $("div [id='other_apis']").show();
                      }
                      if ($("div [id='internal_apis'] div").length > 0) {
                        $("div [id='internal_apis']").show();
                      }
                      if ($("div [id='integrated_apis'] div").length > 0) {
                        $("div [id='integrated_apis']").show();
                      }
                      if ($("div [id='none_apis'] div").length > 0) {
                        $("div [id='none_apis']").show();
                      }
                      if ($("div [id='registereddevice_apis'] div").length > 0) {
                        $("div [id='registereddevice_apis']").show();
                      }
                      if ($("div [id='userlogin_apis'] div").length > 0) {
                        $("div [id='userlogin_apis']").show();
                      }
                      if ($("div [id='doc_apis'] div").length > 0) {
                        $("div [id='doc_apis']").show();
                      }
                      $("div [groupname='" + group_name + "'] div.div_detail_toggle").removeClass("detail_visited");
                      $("div [groupname='" + group_name + "'] div.detail").hide();
                      $("div [groupname='" + group_name + "'] div.detail").removeClass("detail_visited");
                    }

                    /**
                     *li对象 visited事件后 class变化
                     */
                    function li_visited_class(_this) {
                      $("li").removeClass("visited");
                      $(_this).addClass("visited");
                    }

                    /**
                     * 展示api详细信息
                     */
                    function method_click(name, _this) {
                      if (!$("[id='" + name + "']").is(":visible")) {
                        $("[id='" + name + "']").show("fast");

                        $(_this).addClass("detail_visited");
                        $("[id='" + name + "']").addClass("detail_visited");
                      } else {
                        $("[id='" + name + "']").hide("fast");
                        //alert($(_this).attr("class"));
                        $(_this).removeClass("detail_visited");
                        //alert($(_this).attr("class"));
                      }
                    }

                    function highlight(focus) {
                      clearSelection(); //先清空一下上次高亮显示的内容；
                      var flag = 0;
                      var bStart = true;

                      var searchText = $('#searchstr').val().trim(); //获取你输入的关键字；
                      if (searchText == '') {
                        searchText = focus;
                      }
                      var _searchTop = $('#searchstr').offset().top + 30;
                      var _searchLeft = $('#searchstr').offset().left;
                      if ($.trim(searchText) == "" || $.trim(searchText) == '.') {
                        showTips("请输入内容", _searchTop, 3, _searchLeft);
                        return;
                      }
                      var regExp = new RegExp(searchText, 'g'); //创建正则表达式，g表示全局的，如果不用g，则查找到第一个就不会继续向下查找了；

                      var content = getSearchContent();
                      <!-- alert(content); -->
                      if (!regExp.test(content)) {
                        showTips("没找到查找内容", _searchTop, 3, _searchLeft);
                        return;
                      } else {
                        if (sCurText != searchText) {
                          i = 0;
                          sCurText = searchText;
                        }
                      }
                      $("span.api_name").each(function() {
                        var html = $(this).html();
                        var newHtml = html.replace(regExp, '<span class="highlight">' + searchText + '</span>'); //将找到的关键字替换，加上highlight属性；
                        $(this).html(newHtml); //更新
                        if (html != newHtml) {
                          flag = 1;
                        }

                      });
                      $("td[name='code']").each(function() {
                        var html = $(this).html();
                        var newHtml = html.replace(regExp, '<span class="highlight">' + searchText + '</span>'); //将找到的关键字替换，加上highlight属性；
                        $(this).html(newHtml); //更新
                        if (html != newHtml) {
                          flag = 2;
                        }
                      });
                      $("td[name='name']").each(function() {
                        var html = $(this).html();
                        var newHtml = html.replace(regExp, '<span class="highlight">' + searchText + '</span>'); //将找到的关键字替换，加上highlight属性；
                        $(this).html(newHtml); //更新
                        if (html != newHtml) {
                          flag = 2;
                        }
                      });
                      $("td[name='desc']").each(function() {
                        var html = $(this).html();
                        var newHtml = html.replace(regExp, '<span class="highlight">' + searchText + '</span>'); //将找到的关键字替换，加上highlight属性；
                        $(this).html(newHtml); //更新
                        if (html != newHtml) {
                          flag = 2;
                        }
                      });

                      if (flag == 1 || flag == 2) {
                        if (flag == 1) {
                          //跳转到该GROUP所在的分页 //所在group是否可见？
                          <!-- alert($(".highlight").eq(i).parent("span.api_name").is(":visible")); -->
                          if (!$(".highlight").eq(i).parent("span.api_name").is(":visible")) { //不可见
                            var apiName = $(".highlight").eq(i).parent("span.api_name").parents("div.api").attr("groupname");
                            <!-- alert(apiName); -->
                            //右部api内容
                            show_curr_group_apis(apiName);
                            //左部菜单li
                            $("div#groups li").each(function(i) {
                              if ($(this).text() == apiName) {
                                li_visited_class(this);
                              }
                            });
                          }
                        }
                        if ($(".highlight").size() > 1) {
                          var _top = $(".highlight").eq(i).offset().top + $(".highlight").eq(i).height();
                          var _tip = $(".highlight").eq(i).parent().find("strong").text();
                          if (_tip == "") _tip = $(".highlight").eq(i).parent().parent().find("strong").text();
                          var _left = $(".highlight").eq(i).offset().left;
                          var _tipWidth = $("#tip").width();
                          if (_left > $(document).width() - _tipWidth) {
                            _left = _left - _tipWidth;
                          }
                          $("#search_btn").val("查找下一个");
                        } else {
                          var _top = $(".highlight").offset().top + $(".highlight").height();
                          var _tip = $(".highlight").parent().find("strong").text();
                          var _left = $(".highlight").offset().left;
                        }
                        $("html, body").animate({
                          scrollTop: _top - 50
                        }, 100);
                        i++;
                        if (i > $(".highlight").size() - 1) {
                          i = 0;
                        }
                      }
                    }

                    function getSearchContent() {
                      var content = $("span.api_name").text() + $("#busi_err table td[name='code']").text() + $("#busi_err table td[name='name']").text() +
                        $("#busi_err table td[name='desc']").text();
                      return content;
                    }

                    function clearSelection() {
                      $("span.api_name").each(function() {
                        //找到所有highlight属性的元素；
                        $(this).find('.highlight').each(function() {
                          $(this).replaceWith($(this).html()); //将他们的属性去掉；
                        });
                      });
                      $("td[name='code']").each(function() {
                        //找到所有highlight属性的元素；
                        $(this).find('.highlight').each(function() {
                          $(this).replaceWith($(this).html()); //将他们的属性去掉；
                        });
                      });
                      $("td[name='name']").each(function() {
                        //找到所有highlight属性的元素；
                        $(this).find('.highlight').each(function() {
                          $(this).replaceWith($(this).html()); //将他们的属性去掉；
                        });
                      });
                      $("td[name='desc']").each(function() {
                        //找到所有highlight属性的元素；
                        $(this).find('.highlight').each(function() {
                          $(this).replaceWith($(this).html()); //将他们的属性去掉；
                        });
                      });
                    }

                    function showTips(tips, height, time, left) {
                      var windowWidth = document.documentElement.clientWidth;
                      $('.tipsClass').text(tips);
                      $('div.tipsClass').css({
                        'top': height + 'px',
                        'left': left + 'px',
                        'position': 'absolute',
                        'padding': '8px 6px',
                        'background': '#000000',
                        'font-size': 14 + 'px',
                        'font-weight': 900,
                        'margin': '0 auto',
                        'text-align': 'center',
                        'width': 'auto',
                        'color': '#fff',
                        'border-radius': '2px',
                        'opacity': '0.8',
                        'box-shadow': '0px 0px 10px #000',
                        '-moz-box-shadow': '0px 0px 10px #000',
                        '-webkit-box-shadow': '0px 0px 10px #000'
                      }).show();
                      setTimeout(function() {
                        $('div.tipsClass').fadeOut();
                      }, (time * 100));
                    }

                    /**
                     * 固定div的效果，就是当页面往下拉滚动时，用于查找的输入框和按钮始终固定在页面的最顶部，方便继续查找
                     */
                    (function($) {
                      $.fn.fixDiv = function(options) {
                        var defaultVal = {
                          top: 10
                        };
                        var obj = $.extend(defaultVal, options);
                        $this = this;
                        var _top = $this.offset().top;
                        var _left = $this.offset().left;
                        $(window).scroll(function() {
                          var _currentTop = $this.offset().top;
                          var _scrollTop = $(document).scrollTop();
                          if (_scrollTop > _top) {
                            $this.offset({
                              top: _scrollTop + obj.top,
                              left: _left
                            });
                          } else {
                            $this.offset({
                              top: _top,
                              left: _left
                            });
                          }

                        });

                        return $this;
                      };
                    })(jQuery);

                    function showDetailOnWindow(_this) {
                      var method_name = $(_this).parents("div.api").find("span.api_name").html();
                      var content = $(_this).next("div#desc_detail").html();
                      content = content.replace(/&gt;/g, ">");
                      content = content.replace(/&lt;/g, "<");
                      var newWindow =
                        open('', '_blank');
                      newWindow.document.open();
                      newWindow.document.title = method_name;
                      newWindow.document.write(content);
                      newWindow.document.close();
                    }
                ]]>
                </script>
            </head>
            <body>
                <div id="main" class="main">
                    <div class="title">
                        <p>客户端API文档</p>
                        <div id="search_box">
                            <input class="textbox" id="searchstr" type="text" size="10" name="searchstr"/>　
                            <input class="sbttn" id="search_btn" type="button" value="页内查找"/>
                        </div>
                    </div>

                    <div id="top" class="top">
                        <div class="title">API:</div>
                        <!-- 左边 group li -->
                        <div id="groups" class="div_left_groups">
                            <ul>
                                <li></li>
                            </ul>
                        </div>
                        <!-- 右边 主div-->
                        <div id="main_content" class="div_main_content">
                            <div id="doc_apis" class="hide">
                                <span>DOCUMENT</span>
                            </div>
                            <div id="none_apis" class="hide">
                                <span>NONE</span>
                            </div>
                            <div id="registereddevice_apis" class="hide">
                                <span>REGISTERED_DEVICE</span>
                            </div>
                            <div id="userlogin_apis" class="hide">
                                <span>USER_LOGIN</span>
                            </div>
                            <div id="internal_apis" class="hide">
                                <span>INTERNAL</span>
                            </div>
                            <div id="integrated_apis" class="hide">
                                <span>INTEGRATED</span>
                            </div>
                            <div id="other_apis" class="hide">
                                <span>OTHER</span>
                            </div>
                            <xsl:for-each select="/Document/apiList/api">
                                <div class="api">
                                    <xsl:attribute name="groupname">
                                        <xsl:value-of select="groupName"/>
                                    </xsl:attribute>
                                    <a class="hide">
                                        <xsl:value-of select="groupName"/>
                                    </a>
                                    <div class="div_detail_toggle">
                                        <xsl:attribute name="onclick">method_click('d_<xsl:value-of select="methodName"/>', this)
                                        </xsl:attribute>
                                        <span class="api_name">
                                            <xsl:value-of select="methodName"/>
                                        </span>
                                        <span class="api_desc i18n">
                                            <xsl:value-of select="description"/>
                                        </span>
                                    </div>
                                    <div class="detail">
                                        <xsl:attribute name="id">d_<xsl:value-of select="methodName"/>
                                        </xsl:attribute>
                                        <xsl:if test="string-length(detail)&gt;0">
                                            <div>
                                                <a href="javascript:void(0);" onclick="showDetailOnWindow(this);">
                                                    <strong>详细说明</strong>
                                                </a>
                                                <div class="hide" id="desc_detail">
                                                    <xsl:value-of select="detail"/>
                                                </div>
                                            </div>
                                        </xsl:if>
                                        <div id="api_securityLevel">
                                            <strong>安全级别:</strong>
                                            <span>
                                                <xsl:value-of select="securityLevel"/>
                                            </span>
                                        </div>
                                        <xsl:if test="string-length(subSystem)&gt;0">
                                            <div>
                                                <strong>所属子系统:</strong>
                                                <xsl:value-of select="subSystem"/>
                                            </div>
                                        </xsl:if>
                                        <div>
                                            <strong>接口分组:</strong>
                                            <xsl:value-of select="groupName"/>
                                        </div>
                                        <div id="api_state">
                                            <strong>接口状态:</strong>
                                            <span>
                                                <xsl:value-of select="state"/>
                                            </span>
                                        </div>
                                        <xsl:if test="count(exportParams/item)&gt;0">
                                            <div id="api_export">
                                                <strong>隐式导出:</strong>
                                                <xsl:for-each select="exportParams/item">
                                                    <xsl:if test="position()>1">;<![CDATA[  ]]></xsl:if>
                                                    <xsl:value-of select="."/>
                                                </xsl:for-each>
                                            </div>
                                        </xsl:if>
                                        <div>
                                            <strong>接口负责人:</strong>
                                            <xsl:value-of select="methodOwner"/>
                                        </div>
                                        <div class="underline">
                                            <strong>项目负责人:</strong>
                                            <xsl:value-of select="groupOwner"/>
                                        </div>
                                        <div>
                                            <strong>应用级参数列表:</strong>
                                            <xsl:if test="parameterInfoList/child::*">
                                                <table class="tbl_border_collapse">
                                                    <tr>
                                                        <th style="width:10%">必要性</th>
                                                        <th style="width:15%">参数名</th>
                                                        <th style="width:15%">类型</th>
                                                        <th style="width:10%">seq.</th>
                                                        <th style="width:50%">描述</th>
                                                    </tr>
                                                    <xsl:for-each select="parameterInfoList/parameterInfo">
                                                        <tr>
                                                            <td style="text-align:center">
                                                                <xsl:choose>
                                                                    <xsl:when test="isRequired='true'">必选</xsl:when>
                                                                    <xsl:when test="injectOnly='true'">注入</xsl:when>
                                                                    <xsl:otherwise>可选</xsl:otherwise>
                                                                </xsl:choose>
                                                            </td>
                                                            <td style="text-align:center" class="i18n">
                                                                <xsl:value-of select="name"/>
                                                            </td>
                                                            <td style="text-align:center">
                                                                <xsl:choose>
                                                                    <xsl:when test="isList='true'">
                                                                        <xsl:choose>
                                                                            <xsl:when
                                                                                    test="type='int' or type='byte' or type='short' or type='char' or type='boolean' or type='long' or type='float' or type='double'">
                                                                                <xsl:value-of select="type"/>[]
                                                                            </xsl:when>
                                                                            <xsl:otherwise>
                                                                                List&lt;<xsl:value-of select="type"/>&gt;
                                                                            </xsl:otherwise>
                                                                        </xsl:choose>
                                                                    </xsl:when>
                                                                    <xsl:otherwise>
                                                                        <xsl:value-of select="type"/>
                                                                    </xsl:otherwise>
                                                                </xsl:choose>
                                                            </td>
                                                            <td style="text-align:center">
                                                                <xsl:value-of select="sequence"/>
                                                            </td>
                                                            <td style="text-align:left">
                                                                <p class="i18n">
                                                                    <xsl:value-of select="description"/>
                                                                </p>
                                                                <xsl:if test="verifyRegex != ''">
                                                                    <font color="red">
                                                                        正则校验规则:
                                                                    </font>
                                                                    <xsl:if test="verifyMsg != ''">
                                                                        <xsl:value-of select="verifyMsg"/>:
                                                                    </xsl:if>
                                                                    <xsl:value-of select="verifyRegex"/>
                                                                </xsl:if>
                                                                <xsl:if test="serviceInjection != ''">
                                                                    该参数
                                                                    <xsl:if test="injectOnly='true'">仅</xsl:if>
                                                                    <xsl:if test="injectOnly='false'">可</xsl:if>由服务端其他接口隐式注入, 注入参数名为<xsl:value-of
                                                                        select="serviceInjection"/>
                                                                </xsl:if>
                                                            </td>
                                                        </tr>
                                                    </xsl:for-each>
                                                </table>
                                            </xsl:if>
                                        </div>
                                        <div>
                                            <xsl:for-each select="reqStructList/reqStruct">
                                                <div class="div_detail_resp">
                                                    <div><xsl:value-of select="name"/>:
                                                    </div>
                                                    <table class="tbl_no_border_collapse">
                                                        <xsl:for-each select="fieldList/field">
                                                            <tr>
                                                                <td style="width:20%;text-align:left">
                                                                    <font color="green">
                                                                        <xsl:choose>
                                                                            <xsl:when test="isList='true'">
                                                                                <xsl:choose>
                                                                                    <xsl:when
                                                                                            test="type='int' or type='byte' or type='short' or type='char' or type='boolean' or type='long' or type='float' or type='double'">
                                                                                        <xsl:value-of select="type"/>[]
                                                                                    </xsl:when>
                                                                                    <xsl:otherwise>
                                                                                        List&lt;<xsl:value-of select="type"/>&gt;
                                                                                    </xsl:otherwise>
                                                                                </xsl:choose>
                                                                            </xsl:when>
                                                                            <xsl:otherwise>
                                                                                <xsl:value-of select="type"/>
                                                                            </xsl:otherwise>
                                                                        </xsl:choose>
                                                                    </font>
                                                                </td>
                                                                <td style="width:15%;text-align:left">
                                                                    <xsl:value-of select="name"/>
                                                                </td>
                                                                <td style="width:65%;text-align:left" class="i18n">//<xsl:value-of select="desc"/>
                                                                </td>
                                                            </tr>
                                                        </xsl:for-each>
                                                    </table>
                                                </div>
                                            </xsl:for-each>
                                        </div>
                                        <div>
                                            <strong>返回值类型:</strong>
                                            <xsl:value-of select="substring-after(returnType, '$')"/>
                                            <xsl:for-each select="respStructList/respStruct">
                                                <div class="div_detail_resp">
                                                    <div><xsl:value-of select="name"/>:
                                                    </div>
                                                    <table class="tbl_no_border_collapse">
                                                        <xsl:for-each select="fieldList/field">
                                                            <tr>
                                                                <td style="width:20%;text-align:left">
                                                                    <font color="green">
                                                                        <xsl:choose>
                                                                            <xsl:when test="isList='true'">
                                                                                <xsl:choose>
                                                                                    <xsl:when
                                                                                            test="type='int' or type='byte' or type='short' or type='char' or type='boolean' or type='long' or type='float' or type='double'">
                                                                                        <xsl:value-of select="type"/>[]
                                                                                    </xsl:when>
                                                                                    <xsl:otherwise>
                                                                                        List&lt;<xsl:value-of select="type"/>&gt;
                                                                                    </xsl:otherwise>
                                                                                </xsl:choose>
                                                                            </xsl:when>
                                                                            <xsl:otherwise>
                                                                                <xsl:value-of select="type"/>
                                                                            </xsl:otherwise>
                                                                        </xsl:choose>
                                                                    </font>
                                                                </td>
                                                                <td style="width:15%;text-align:left">
                                                                    <xsl:value-of select="name"/>
                                                                </td>
                                                                <td style="width:65%;text-align:left" class="i18n">//<xsl:value-of select="desc"/>
                                                                </td>
                                                            </tr>
                                                        </xsl:for-each>
                                                    </table>
                                                </div>
                                            </xsl:for-each>
                                        </div>
                                        <div class="div_busi_err">
                                            <strong>业务异常列表:</strong>
                                            <xsl:if test="errorCodeList/child::*">
                                                <table class="tbl_border_collapse">
                                                    <tr>
                                                        <th>code</th>
                                                        <th>name</th>
                                                        <th>描述</th>
                                                    </tr>
                                                    <xsl:for-each select="errorCodeList/errorCode">
                                                        <tr>
                                                            <td>
                                                                <xsl:value-of select="code"/>
                                                            </td>
                                                            <td>
                                                                <xsl:value-of select="name"/>
                                                            </td>
                                                            <td class="i18n">
                                                                <xsl:value-of select="desc"/>
                                                            </td>
                                                        </tr>
                                                    </xsl:for-each>
                                                </table>
                                            </xsl:if>
                                        </div>
                                    </div>
                                    <!-- end of detail div -->
                                </div>
                                <!-- end of api div -->
                            </xsl:for-each>
                        </div>
                        <!-- end of main_content div -->
                        <div style="clear:both;"></div>
                    </div>
                    <!-- end of top div -->
                    <div id="comm_sys_param" class="div_comm_sys_param tbl_err_code">
                        <div class="title">系统级参数列表:</div>
                        <table class="tbl_border_collapse">
                            <tr>
                                <th style="width:15%">参数名</th>
                                <th style="width:85%">描述</th>
                            </tr>
                            <xsl:for-each select="/Document/systemParameterInfoList/systemParameterInfo">
                                <tr>
                                    <td>
                                        <xsl:value-of select="name"/>
                                    </td>
                                    <td class="i18n">
                                        <xsl:value-of select="desc"/>
                                    </td>
                                </tr>
                            </xsl:for-each>
                        </table>
                    </div>
                    <!-- end of comm_sys_param div -->
                    <div id="comm_return" class="div_comm_return">
                        <div class="title">通用返回值结构:</div>
                        <xsl:for-each select="/Document/respStructList/respStruct">
                            <div class="div_resp">
                                <div><xsl:value-of select="name"/>:
                                </div>
                                <table class="tbl_no_border_collapse">
                                    <xsl:for-each select="fieldList/field">
                                        <tr>
                                            <td style="width:20%;text-align:left">
                                                <font color="green">
                                                    <xsl:if test="isList='true'">List&lt;</xsl:if>
                                                    <xsl:value-of select="type"/>
                                                    <xsl:if test="isList='true'">&gt;</xsl:if>
                                                </font>
                                            </td>
                                            <td style="width:15%;text-align:left">
                                                <xsl:value-of select="name"/>
                                            </td>
                                            <td style="width:65%;text-align:left" class="i18n">//<xsl:value-of select="desc"/>
                                            </td>
                                        </tr>
                                    </xsl:for-each>
                                </table>
                            </div>
                        </xsl:for-each>
                    </div>
                    <!-- end of comm_return div -->
                    <div id="comm_err" class="div_comm_err">
                        <div class="title">通用异常列表:</div>
                        <xsl:if test="/Document/codeList/child::*">
                            <div class="tbl_err_code">
                                <table class="tbl_border_collapse">
                                    <tr>
                                        <th style="width:15%">错误码</th>
                                        <th style="width:20%">名称</th>
                                        <th style="width:65%">描述</th>
                                    </tr>
                                    <xsl:for-each select="/Document/codeList/code">
                                        <xsl:if test="code &lt; 0">
                                            <tr>
                                                <td>
                                                    <xsl:value-of select="code"/>
                                                </td>
                                                <td>
                                                    <xsl:value-of select="name"/>
                                                </td>
                                                <td class="i18n">
                                                    <xsl:value-of select="desc"/>
                                                </td>
                                            </tr>
                                        </xsl:if>
                                    </xsl:for-each>
                                </table>
                            </div>
                        </xsl:if>
                    </div>
                    <!-- end of comm_err div -->
                    <div id="busi_err" class="div_comm_err">
                        <div class="title">业务异常列表:</div>
                        <xsl:if test="/Document/codeList/child::*">
                            <div class="tbl_err_code">
                                <table class="tbl_border_collapse">
                                    <tr>
                                        <th style="width:12%">服务</th>
                                        <th style="width:12%">需要客户端处理</th>
                                        <th style="width:12%">错误码</th>
                                        <th style="width:26%">名称</th>
                                        <th style="width:38%">描述</th>
                                    </tr>
                                    <xsl:for-each select="/Document/codeList/code">
                                        <xsl:if test="code &gt; 0">
                                            <tr>
                                                <td>
                                                    <xsl:value-of select="service"/>
                                                </td>
                                                <td style="text-align: center">
                                                    <xsl:choose>
                                                        <xsl:when test="isDesign='true'">
                                                            <font color="red">是</font>
                                                        </xsl:when>
                                                        <xsl:otherwise>
                                                            <font color="green">否</font>
                                                        </xsl:otherwise>
                                                    </xsl:choose>
                                                </td>
                                                <td name="code" style="text-align:right">
                                                    <xsl:value-of select="code"/>
                                                </td>
                                                <td name="name">
                                                    <xsl:value-of select="name"/>
                                                </td>
                                                <td name="desc" class="i18n">
                                                    <xsl:value-of select="desc"/>
                                                </td>
                                            </tr>
                                        </xsl:if>
                                    </xsl:for-each>
                                </table>
                            </div>
                        </xsl:if>
                    </div>
                    <!-- end of comm_err div -->
                </div>
                <!-- end of main div -->
                <div class="tipsClass"></div>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
