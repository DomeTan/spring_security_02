/**
 * 全局js异常处理。
 */
$(document).ajaxError(function (event, jqxhr, settings, thrownError) {
    alert(JSON.parse(jqxhr.responseText)['error']);
    // location.reload();
});
/**
 * 全局CSRF设置。
 */
$(document).ajaxSend(function (e, xhr, options) {
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    xhr.setRequestHeader(header, token);
});

/**
 * 校验字符串是否不为空，并且不包含空格，和不等于undefined
 */
function isNotBlank(str) {
    return !isBlank(str);
}

/**
 * 校验字符串是否为空，或者包含空格，或者等于undefined
 */
function isBlank(str) {
    return str === null || str === undefined || str.trim() === '';
}
/**
 * @param pageTotal  总页数
 * @param currentPage   当前页
 * @param ulId  显示页码的ul的id值
 * @param findPageFunctionName  分页查询的js方法名
 */
function showPage(pageTotal, currentPage, ulId, findPageFunctionName) {
    var pagination = $("#"+ulId+"");
    pagination.empty();

    if (pageTotal > 7){
        var current = currentPage-2;
        var flag = true;
        var firstPoint = false;
        var lastPoint = true;
        if (currentPage == 0){
            pagination.append("<li class=\"disabled\"><span><i class=\"fa fa-angle-left\"></i></span></li>");
            pagination.append("<li class=\"active\"><span>"+1+"</span></li>");
        }else {
            pagination.append("<li><a href=\"javascript:"+findPageFunctionName+"("+"'"+(currentPage-1)+"'"+");\"><i class=\"fa fa-angle-left\"></i></a></li>");
            pagination.append("<li><a href=\"javascript:"+findPageFunctionName+"("+"'"+0+"'"+");\">"+1+"</a></li>");
        }
        if (currentPage>3){
            pagination.append("<li class=\"disabled\"><span>...</span></li>");
            firstPoint = true;
        }
        for (var i = 1; i < pageTotal-1; i++){
            if (firstPoint){
                if (currentPage >= pageTotal-2) {
                    if (flag){
                        if (currentPage == pageTotal-2) {
                            current -= 1;
                        }else {
                            current -= 2;
                        }
                        flag = false;
                    }
                }
                if (i >= current  && currentPage+2 >= i){
                    if (i == currentPage){
                        pagination.append("<li class=\"active\"><span>"+(i+1)+"</span></li>");
                    }else {
                        pagination.append("<li><a href=\"javascript:"+findPageFunctionName+"("+"'"+i+"'"+");\">"+(i+1)+"</a></li>");
                    }
                }
            }else {
                if (i == currentPage){
                    pagination.append("<li class=\"active\"><span>"+(i+1)+"</span></li>");
                }else {
                    if (i < 5){
                        pagination.append("<li><a href=\"javascript:"+findPageFunctionName+"("+"'"+i+"'"+");\">"+(i+1)+"</a></li>");
                    }
                }
            }
        }

        if (currentPage+3 < pageTotal && currentPage+4 != pageTotal){
            if (lastPoint){
                pagination.append("<li class=\"disabled\"><span>...</span></li>");
                lastPoint = false;
            }
        }

        if (pageTotal != 1) {
            if (currentPage == pageTotal-1){
                pagination.append("<li class=\"active\"><span>"+pageTotal+"</span></li>");
            }else {
                pagination.append("<li><a href=\"javascript:"+findPageFunctionName+"("+"'"+(pageTotal-1)+"'"+");\">"+pageTotal+"</a></li>");
            }
        }

        if (currentPage == pageTotal-1){
            pagination.append("<li class=\"disabled\"><span><i class=\"fa fa-angle-right\"></i></span></li>");
        }else {
            pagination.append("<li><a href=\"javascript:"+findPageFunctionName+"("+"'"+(currentPage+1)+"'"+");\"><i class=\"fa fa-angle-right\"></i></a></li>");
        }

    }else {
        if (currentPage == 0){
            pagination.append("<li class=\"disabled\"><span><i class=\"fa fa-angle-left\"></i></span></li>");
        }else {
            pagination.append("<li><a href=\"javascript:"+findPageFunctionName+"("+"'"+(currentPage-1)+"'"+");\"><i class=\"fa fa-angle-left\"></i></a></li>");
        }

        for (var i = 0; i < pageTotal; i++){
            if (currentPage == i){
                pagination.append("<li class=\"active\"><span>"+(i+1)+"</span></li>");
            }else {
                pagination.append("<li><a href=\"javascript:"+findPageFunctionName+"("+"'"+i+"'"+");\">"+(i+1)+"</a></li>");
            }
        }

        if (currentPage == pageTotal-1){
            pagination.append("<li class=\"disabled\"><span><i class=\"fa fa-angle-right\"></i></span></li>");
        }else {
            pagination.append("<li><a href=\"javascript:"+findPageFunctionName+"("+"'"+(currentPage+1)+"'"+");\"><i class=\"fa fa-angle-right\"></i></a></li>");
        }
    }
}
/**
 * @param pageTotal  总页数
 * @param currentPage   当前页
 * @param ulId  显示页码的ul的id值
 * @param findPageFunctionName  分页查询的js方法名
 */
function showNextPage(currentPage, ulId, findPageFunctionName) {
    var pagination = $("#"+ulId+"");
    pagination.empty();
    if (currentPage == 0){
        pagination.append("<li class=\"disabled\"><span><i class=\"fa fa-angle-left\"></i></span></li>");
        pagination.append("<li ><a href=\"javascript:"+findPageFunctionName+"("+"'"+(currentPage+1)+"'"+");\"><i class=\"fa fa-angle-right\"></i></a></li>");
    }else {
        pagination.append("<li><a href=\"javascript:"+findPageFunctionName+"("+"'"+(currentPage-1)+"'"+");\"><i class=\"fa fa-angle-left\"></i></a></li>");
        pagination.append("<li><a href=\"javascript:"+findPageFunctionName+"("+"'"+(currentPage+1)+"'"+");\"><i class=\"fa fa-angle-right\"></i></a></li>");
    }
}

/**
 * 判断值是否为空
 */
function isNull(value) {
    return value == undefined ? "-":(value == "" ? "-":value);
}

/**
 * 解析本地日期字符串“yyyy-MM-dd”
 */
function parseLocalDate(dateString) {
    var dates = dateString.split(/\D/);
    return new Date(dates[0], dates[1] - 1, dates[2]);
}

/**
 * 解析本地时间字符串“yyyy-MM-dd HH:mm:ss”
 */
function parseLocalDateTime(timeString) {
    var times = timeString.split(/\D/);
    return new Date(times[0], times[1] - 1, times[2], times[3], times[4], times[5]);
}

/**
 * 格式化Date对象为“yyyy-MM-dd HH:mm”。
 */
function formatDate(date) {
    return date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate() + " "
        + date.getHours() + ':' + date.getMinutes();
}

/**
 * 返回两个日期的天数差(向上取整)。
 */
function intervalDays(startDate, endDate) {
    var millisecondsPerDay = 24 * 60 * 60 * 1000;
    return Math.ceil((endDate - startDate) / millisecondsPerDay);
}

/*
 * @param fmt 时间格式
 * @returns {*} 2018-12-26 09:11:48 格式化之后
 * @constructor
 */
Date.prototype.Format = function(fmt)
{
    var format = {
        "M+" : this.getMonth()+1,                 //月份
        "d+" : this.getDate(),                    //日
        "h+" : this.getHours(),                   //小时
        "m+" : this.getMinutes(),                 //分
        "s+" : this.getSeconds(),                 //秒
        "q+" : Math.floor((this.getMonth()+3)/3), //季度
        "S"  : this.getMilliseconds()             //毫秒
    };
    if(/(y+)/.test(fmt))
        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
    for(var k in format)
        if(new RegExp("("+ k +")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (format[k]) : (("00"+ format[k]).substr((""+ format[k]).length)));
    return fmt;
};

/**
 * 将毫秒转换成分钟和秒。
 */
function millisToMinutesAndSeconds(millis) {
    var minutes = Math.floor(millis / 60000);
    var seconds = ((millis % 60000) / 1000).toFixed(0);
    return minutes + ":" + (seconds < 10 ? '0' : '') + seconds;
}

/**
 * 时间戳          转换      时间
 * 1575709324442 -> 2019-12-07 17:02:04
 */
function millisToDate(millis) {
    var date = new Date(millis);
    Y = date.getFullYear() + '-';
    M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
    D = date.getDate() + ' ';
    h = date.getHours() + ':';
    m = date.getMinutes() + ':';
    s = date.getSeconds();
    return (Y+M+D+h+m+s);
}

/**
 * 转化字节为适合人类阅读的单位。
 */
function transferBytesWithAutoUnit(number) {
    if (number > 1073741824) {
        return (number >> 30) + "G";
    }

    if (number > 1048576) {
        return (number >> 20) + "M";
    }

    if (number > 1024) {
        return (number >> 10) + "K";
    }

    return number + "Bytes";
}
