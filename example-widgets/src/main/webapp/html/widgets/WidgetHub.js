// Util Class: Get the configuration, set the value of HTML fields: input, select, textarea
function FieldUtil(type, id) {
    TYPE = {radio: 'radio', checkbox: 'checkbox', textarea: 'textarea', select: 'select'};

    this.type = type;
    this.id = id; // if type is radio or checkbox, id is the name of the group
    var node;
    if (type != 'radio' && type != 'checkbox')
        node = document.getElementById(id);


    function getLabel(obj) {
        if (obj.getAttribute('id') == null)
            return null;
        labels = document.getElementsByTagName('label');
        for (var i = 0; i < labels.length; ++i) {
            if (labels[i].getAttribute('for') == obj.getAttribute('id')) {
                return labels[i];
            }
        }
        return null;
    }

    function getLabelTxt(obj) {
        label = getLabel(obj);
        if (label == null)
            return "";
        else
            return label.innerHTML;
    }

    function setLabelTxt(obj, txt) {
        label = getLabel(obj);
        if (label != null)
            label.innerHTML = txt;
    };

    this.getValue = function () {
        var value;
        if (this.type == 'select') {
            value = [];
            options = node.getElementsByTagName('option');
            for (var j = 0; j < options.length; ++j) {
                if (options[j].selected)
                    value.push(j);
            }
        } else if (this.type == 'textarea' || this.type == 'text') {
            value = node.value;
        } else if (this.type == 'radio' || this.type == 'checkbox') {
            // Get all values from all items in the same group
            items = document.getElementsByName(this.id);
            value = [];
            for (var k = 0; k < items.length; ++k) {
                if (items[k].checked)
                    value.push(k);
            }
        }
        return {type: this.type, value: value};
    };

    this.setValue = function (value) {
        if (this.type == 'radio' || this.type == 'checkbox') {
            items = document.getElementsByName(this.id);
            for (var j = 0; j < items.length; ++j)
                items[j].checked = false;
            for (var j = 0; j < value.length; ++j) {
                if (value[j] < items.length)
                    items[value[j]].checked = true;
            }
        } else if (this.type == 'textarea' || this.type == 'text') {
            node.value = value;
        } else if (this.type == 'select') {
            options = node.getElementsByTagName('option');
            for (var j = 0; j < options.length; ++j)
                options[j].selected = false;
            for (var j = 0; j < value.length; ++j) {
                if (value[j] < options.length)
                    options[value[j]].selected = true;
            }
        }
    };

    this.getNodeConfiguration = function () {
        var res = this.getValue();
        res.id = this.id;
        if (this.type == 'text' || this.type == 'textarea') {
            res.label = getLabelTxt(node);
            res["@class"] = "OptionText";
        } else if (this.type == 'radio' || this.type == 'checkbox') {
            items = document.getElementsByName(this.id);
            tmp = [];
            for (var j = 0; j < items.length; ++j) {
                tmp.push({
                    value: items[j].getAttribute('value'),
                    id: items[j].getAttribute('id'),
                    label: getLabelTxt(items[j])
                });
            }
            res.items = tmp;
            res["@class"] = "OptionRadioCheck";
        } else if (this.type == 'select') {
            res.label = getLabelTxt(node);
            res.multiple = node.getAttribute('multiple');
            res.size = node.getAttribute('size');

            options = node.getElementsByTagName('option');
            tmp = [];
            for (var j = 0; j < options.length; ++j) {
                tmp.push({value: options[j].getAttribute('value'), text: options[j].innerHTML});
            }
            res.items = tmp;
            res["@class"] = "OptionSelect";
        }
        return res;
    };
}

// Static method
FieldUtil.createInstance = function (obj) {
    var fieldUtil = null;
    if (obj.nodeName == 'INPUT') {
        type = obj.getAttribute('type');
        if (type == 'hidden')
            type = 'text';
        if (type != 'radio' && type != 'checkbox' && type != 'text')
            return null;
        id = (type == 'radio' || type == 'checkbox') ? obj.getAttribute('name') : obj.getAttribute('id');
        fieldUtil = new FieldUtil(type, id);
    } else if (obj.nodeName == 'TEXTAREA') {
        fieldUtil = new FieldUtil('textarea', obj.getAttribute('id'));
    } else if (obj.nodeName == 'SELECT') {
        fieldUtil = new FieldUtil('select', obj.getAttribute('id'));
    }
    return fieldUtil;
};


// Util Class: get all HTML fields in the source code
function FieldDiscoveryUtil() {
    var lstName = {};
    var result = [];

    function traverse(obj) {
        var obj = obj || document.getElementsByTagName('body')[0];

        var fieldUtil = FieldUtil.createInstance(obj);
        if (fieldUtil != null) {
            if (fieldUtil.type == 'radio' || fieldUtil.type == 'checkbox') {
                if (lstName[id] == null || !lstName[id]) {
                    // Mark this name so we dont find again
                    lstName[id] = true;
                } else
                    return;
            }
            if (fieldUtil.id != null) // Discard the input without id
                result.push(fieldUtil.getNodeConfiguration());
        } else if (obj.hasChildNodes()) {
            var child = obj.firstChild;
            while (child) {
                if (child.nodeType === 1 && child.nodeName != 'SCRIPT') {
                    traverse(child);
                }
                child = child.nextSibling;
            }
        }
    }

    this.getFields = function () {
        lstName = {};
        result = [];
        traverse();
        return result;
    };
}

/**
 * Util class to enable the communication between a client widget (or client GUI of a server widget) and the Cooperator
 */
function JavaScriptCommunication() {
    var id;
    var callingMap = {};
    var rpcMap = {};

    function postMessageToCooperator(data) {
        if (id != "") // Ignore if a mash-up is not a widget (it is a main window) (and its id will be "")
            parent.postMessage(JSON.stringify(data), "*");
    }

    function postMessageToWidget(id, data) {
        // To send data to another widget (in another window object), we need the platform as the intermediate, because we can not access the window object of the other widget if our widgets are in different domains
        parent.postMessage("|" + id + "|" + JSON.stringify(data), "*");
    }

    this.setId = function (widgetId) {
        id = widgetId;
    }

    /**
     * Register a method so that the Cooperator can call this method
     */
    this.register = function (methodId, method) {
        rpcMap[methodId] = method;
    }

    /**
     * Call a method from the Cooperator
     */
    this.call = function (methodId, param, callback) {
        postMessageToCooperator({"id": id, "methodId": methodId, "role": "call", "param": param});
        callingMap[methodId] = callback;
    }

    /**
     * Call a method from other widget (Used to transfer output data to other client widget)
     */
    this.publish = function (id, methodId, param, callback) {
        postMessageToWidget(id, {"methodId": methodId, "role": "call", "param": param});
    }

    function messageListener(e) {
        try {
            var data = JSON.parse(e.data);
            var methodId = data["methodId"];
            if (data.role == "call") {
                if (rpcMap[methodId] != null) {
                    var response = rpcMap[methodId](data.param);
                    if (response == null)
                        response = {};
                    postMessageToCooperator({"id": id, "methodId": methodId, "role": "return", "data": response});
                }
            } else if (data.role == "return") {
                if (callingMap[methodId] != null)
                    callingMap[methodId](data.data);
            }
        } catch (err) {
            // The message is not from platform, don't care
        }
    }

    if (window.attachEvent) { //IE
        window.attachEvent("onmessage", messageListener, false);
    } else if (window.addEventListener) {
        window.addEventListener("message", messageListener, false);
    }
}

/**
 * Util class to manipulate the collected input of a client widget (for all of its input terminals)
 */
function WidgetInputData() {
    var terminals = {};
    var data = {};
    var index = {};
    var counter = 0;

    /**
     * Add an input terminal
     * @terminal: name of the input terminal
     * @source: name(s) of output terminal(s) connected to the concerning input terminal (It is an array of String (if @multiple is true) or a single String)
     */
    this.addTerminal = function (terminal, multiple, source) {
        terminals[terminal] = {"terminal": terminal, "multiple": multiple};
        if (multiple) {
            counter += source.length;
            data[terminal] = [];
            for (var i = 0; i < source.length; ++i) {
                data[terminal].push(null);
                index[source[i]] = i;
            }
        } else {
            counter++;
            data[terminal] = null;
        }
    }

    this.receiveData = function (terminal, source, d) {
        if (terminals[terminal]["multiple"]) {
            if (data[terminal][index[source]] == null)
                counter--;
            data[terminal][index[source]] = d;
        } else {
            if (data[terminal] == null)
                counter--;
            data[terminal] = d;
        }
    }

    this.clearInputData = function () {
        counter = 0;
        for (var terminal in terminals) {
            if (terminals[terminal]["multiple"]) {
                for (var p in index) {
                    counter++;
                }
                data[terminal] = [];
            } else {
                counter++;
                data[terminal] = null;
            }
        }
    }

    /**
     * Check if all terminals has received data
     */
    this.isAllDataReceived = function () {
        return (counter == 0);
    }

    this.getData = function () {
        return data;
    }
}

/**
 * Websocket communication util.
 */
function Autobahn(websocketserver, websocketrealm) {
    var wampSession;
    var connection;
    var subscriptions = [];
    var flag;

    this.isOpen = function () {
        return flag;
    }

    this.open = function (callback) {
        connection = new autobahn.Connection({
            "url": websocketserver,
            "realm": websocketrealm
        });
        connection.onopen = function (session) {
            wampSession = session;
            flag = true;
            callback();
        }
        connection.open();
    }

    this.close = function () {
        connection.close();
        flag = false;
    }

    this.publish = function (eventId, message) {
        wampSession.publish(eventId, [message]);
    }

    this.subscribe = function (eventId, callback) {
        wampSession.subscribe(eventId, function (message) {
            callback(message);
        }).then(
            function (subscription) {
                subscriptions.push(subscription);
            }
        );
    }

    this.unsubscribe = function () {
        for (var i = 0; i < subscriptions.length; ++i) {
            subscriptions[i].unsubscribe();
        }
        subscriptions = [];
    }

    this.call = function (functionId, message, callback) {
        wampSession.call(functionId, [message]).then(function (response) {
            callback(response);
        });
    }
}

!function (n) {
    "use strict";

    function t(n, t) {
        var r = (65535 & n) + (65535 & t), e = (n >> 16) + (t >> 16) + (r >> 16);
        return e << 16 | 65535 & r
    }

    function r(n, t) {
        return n << t | n >>> 32 - t
    }

    function e(n, e, o, u, c, f) {
        return t(r(t(t(e, n), t(u, f)), c), o)
    }

    function o(n, t, r, o, u, c, f) {
        return e(t & r | ~t & o, n, t, u, c, f)
    }

    function u(n, t, r, o, u, c, f) {
        return e(t & o | r & ~o, n, t, u, c, f)
    }

    function c(n, t, r, o, u, c, f) {
        return e(t ^ r ^ o, n, t, u, c, f)
    }

    function f(n, t, r, o, u, c, f) {
        return e(r ^ (t | ~o), n, t, u, c, f)
    }

    function i(n, r) {
        n[r >> 5] |= 128 << r % 32, n[(r + 64 >>> 9 << 4) + 14] = r;
        var e, i, a, h, d, l = 1732584193, g = -271733879, v = -1732584194, m = 271733878;
        for (e = 0; e < n.length; e += 16) i = l, a = g, h = v, d = m, l = o(l, g, v, m, n[e], 7, -680876936), m = o(m, l, g, v, n[e + 1], 12, -389564586), v = o(v, m, l, g, n[e + 2], 17, 606105819), g = o(g, v, m, l, n[e + 3], 22, -1044525330), l = o(l, g, v, m, n[e + 4], 7, -176418897), m = o(m, l, g, v, n[e + 5], 12, 1200080426), v = o(v, m, l, g, n[e + 6], 17, -1473231341), g = o(g, v, m, l, n[e + 7], 22, -45705983), l = o(l, g, v, m, n[e + 8], 7, 1770035416), m = o(m, l, g, v, n[e + 9], 12, -1958414417), v = o(v, m, l, g, n[e + 10], 17, -42063), g = o(g, v, m, l, n[e + 11], 22, -1990404162), l = o(l, g, v, m, n[e + 12], 7, 1804603682), m = o(m, l, g, v, n[e + 13], 12, -40341101), v = o(v, m, l, g, n[e + 14], 17, -1502002290), g = o(g, v, m, l, n[e + 15], 22, 1236535329), l = u(l, g, v, m, n[e + 1], 5, -165796510), m = u(m, l, g, v, n[e + 6], 9, -1069501632), v = u(v, m, l, g, n[e + 11], 14, 643717713), g = u(g, v, m, l, n[e], 20, -373897302), l = u(l, g, v, m, n[e + 5], 5, -701558691), m = u(m, l, g, v, n[e + 10], 9, 38016083), v = u(v, m, l, g, n[e + 15], 14, -660478335), g = u(g, v, m, l, n[e + 4], 20, -405537848), l = u(l, g, v, m, n[e + 9], 5, 568446438), m = u(m, l, g, v, n[e + 14], 9, -1019803690), v = u(v, m, l, g, n[e + 3], 14, -187363961), g = u(g, v, m, l, n[e + 8], 20, 1163531501), l = u(l, g, v, m, n[e + 13], 5, -1444681467), m = u(m, l, g, v, n[e + 2], 9, -51403784), v = u(v, m, l, g, n[e + 7], 14, 1735328473), g = u(g, v, m, l, n[e + 12], 20, -1926607734), l = c(l, g, v, m, n[e + 5], 4, -378558), m = c(m, l, g, v, n[e + 8], 11, -2022574463), v = c(v, m, l, g, n[e + 11], 16, 1839030562), g = c(g, v, m, l, n[e + 14], 23, -35309556), l = c(l, g, v, m, n[e + 1], 4, -1530992060), m = c(m, l, g, v, n[e + 4], 11, 1272893353), v = c(v, m, l, g, n[e + 7], 16, -155497632), g = c(g, v, m, l, n[e + 10], 23, -1094730640), l = c(l, g, v, m, n[e + 13], 4, 681279174), m = c(m, l, g, v, n[e], 11, -358537222), v = c(v, m, l, g, n[e + 3], 16, -722521979), g = c(g, v, m, l, n[e + 6], 23, 76029189), l = c(l, g, v, m, n[e + 9], 4, -640364487), m = c(m, l, g, v, n[e + 12], 11, -421815835), v = c(v, m, l, g, n[e + 15], 16, 530742520), g = c(g, v, m, l, n[e + 2], 23, -995338651), l = f(l, g, v, m, n[e], 6, -198630844), m = f(m, l, g, v, n[e + 7], 10, 1126891415), v = f(v, m, l, g, n[e + 14], 15, -1416354905), g = f(g, v, m, l, n[e + 5], 21, -57434055), l = f(l, g, v, m, n[e + 12], 6, 1700485571), m = f(m, l, g, v, n[e + 3], 10, -1894986606), v = f(v, m, l, g, n[e + 10], 15, -1051523), g = f(g, v, m, l, n[e + 1], 21, -2054922799), l = f(l, g, v, m, n[e + 8], 6, 1873313359), m = f(m, l, g, v, n[e + 15], 10, -30611744), v = f(v, m, l, g, n[e + 6], 15, -1560198380), g = f(g, v, m, l, n[e + 13], 21, 1309151649), l = f(l, g, v, m, n[e + 4], 6, -145523070), m = f(m, l, g, v, n[e + 11], 10, -1120210379), v = f(v, m, l, g, n[e + 2], 15, 718787259), g = f(g, v, m, l, n[e + 9], 21, -343485551), l = t(l, i), g = t(g, a), v = t(v, h), m = t(m, d);
        return [l, g, v, m]
    }

    function a(n) {
        var t, r = "";
        for (t = 0; t < 32 * n.length; t += 8) r += String.fromCharCode(n[t >> 5] >>> t % 32 & 255);
        return r
    }

    function h(n) {
        var t, r = [];
        for (r[(n.length >> 2) - 1] = void 0, t = 0; t < r.length; t += 1) r[t] = 0;
        for (t = 0; t < 8 * n.length; t += 8) r[t >> 5] |= (255 & n.charCodeAt(t / 8)) << t % 32;
        return r
    }

    function d(n) {
        return a(i(h(n), 8 * n.length))
    }

    function l(n, t) {
        var r, e, o = h(n), u = [], c = [];
        for (u[15] = c[15] = void 0, o.length > 16 && (o = i(o, 8 * n.length)), r = 0; 16 > r; r += 1) u[r] = 909522486 ^ o[r], c[r] = 1549556828 ^ o[r];
        return e = i(u.concat(h(t)), 512 + 8 * t.length), a(i(c.concat(e), 640))
    }

    function g(n) {
        var t, r, e = "0123456789abcdef", o = "";
        for (r = 0; r < n.length; r += 1) t = n.charCodeAt(r), o += e.charAt(t >>> 4 & 15) + e.charAt(15 & t);
        return o
    }

    function v(n) {
        return unescape(encodeURIComponent(n))
    }

    function m(n) {
        return d(v(n))
    }

    function p(n) {
        return g(m(n))
    }

    function s(n, t) {
        return l(v(n), v(t))
    }

    function C(n, t) {
        return g(s(n, t))
    }

    function A(n, t, r) {
        return t ? r ? s(t, n) : C(t, n) : r ? m(n) : p(n)
    }

    "function" == typeof define && define.amd ? define(function () {
        return A
    }) : "object" == typeof module && module.exports ? module.exports = A : n.md5 = A
}(this);

//# sourceMappingURL=md5.min.js.map


/**
 * Cooperator. Main class of the JavaScript utilities.
 */
function WidgetHub(config, run, loadSetting) {
    var id = "";
    var self = this;
    var input;
    var output;
    var startRunningTime = null;
    var endRunningTime = null;
    var hashValue = null;
    var comm = new JavaScriptCommunication();
    var bahn;

    this.setConfig = function (config) {
        self.config = config;
    };

    this.setRun = function (run) {
        self.run = run;
    };

    this.setLoadSetting = function (loadSetting) {
        self.loadSetting = loadSetting;
    };

    this.setGetParam = function (getParam) {
        self.getParam = getParam;
    };

    this.returnOutput = function (data) {
        endRunningTime = Date();
        hashValue = md5(JSON.stringify(data));

        output = data;
        var nameOfOutputTerminal;
        for (var i = 0; i < self.config.terminals.length; ++i) {
            if (self.config.terminals[i].type == "output") {
                nameOfOutputTerminal = self.config.terminals[i].name;
                break;
            }
        }
        comm.call("getSubscriber", {"output": nameOfOutputTerminal}, function (response) {
            // Transmit data to successor client widgets
            for (var i = 0; i < response.subscriber.length; ++i) {
                var subscriber = response.subscriber[i];
                comm.publish(subscriber["id"], "receive", {
                    "id": id,
                    "output": nameOfOutputTerminal,
                    "input": subscriber["input"],
                    "data": data
                }, function () {
                });
            }

            // Transmit data to successor server widget (by publishing event), if any
            if (response["server"] != null) {
                if (!bahn.isOpen()) {
                    bahn.open(function () {
                        publish(response["server"]);
                    });
                } else {
                    publish(response["server"]);
                }
            }
        });

        function publish(eventId) {
            bahn.publish(eventId, JSON.stringify(data));
        }
    };

    this.sendRunRequest = function () {
        comm.call("run", {}, function () {
        });
    };

    this.sendError = function (message) {
        comm.call("error", {"message": message});
    };

    if (config != null)
        this.setConfig(config);
    if (run != null)
        this.setRun(run);
    if (loadSetting != null)
        this.setLoadSetting(loadSetting);

    /**
     * When the widget frame is loaded, The Cooperator calls this method
     */
    comm.register("setup", function (param) {
        id = param["id"];
        comm.setId(id);
        bahn = new Autobahn(param["websocketserver"], param["websocketrealm"])
        comm.call("configurate", {"config": self.config}, function (data) {
        });
    });

    /**
     * Used to retrieve parameters in the client GUI of server widgets
     */
    comm.register("getParam", function () {
        return self.getParam();
    });

    comm.register("getOption", function () {
        return {"options": new FieldDiscoveryUtil().getFields()};
    });

    comm.register("setOption", function (param) {
        if (self.loadSetting != null) {
            settings = {};
            for (var i = 0; i < param.fields.length; ++i) {
                settings[param.fields[i].id] = param.fields[i].value;
            }
            self.loadSetting(settings);
        }

        for (var i = 0; i < param.fields.length; ++i) {
            try {
                new FieldUtil(param.fields[i].type, param.fields[i].id).setValue(param.fields[i].value);
            } catch (err) {
                console.log("Cannot set the value for field " + param.fields[i].id);
            }
        }
    });

    comm.register("setupInput", function (param) {
        input = new WidgetInputData(); // Reset the input data object
        if (bahn.isOpen()) {
            bahn.unsubscribe(); // Unsubscribe to all server widgets' output events, if any
        }

        for (var key in param) {
            if (param[key].constructor === Array) { // input terminal that receives data from multiple output terminals
                var source = [];
                for (var i = 0; i < param[key].length; ++i) {
                    source.push(param[key][i]["id"] + param[key][i]["output"]);

                    if (param[key][i]["server"] != null) { // input temrminal that receives data from output terminal of server widget
                        subscribeToServerWidget(key, param[key][i]["id"] + param[key][i]["output"], param[key][i]["server"]["eventId"], param[key][i]["server"]["outputRpc"]);
                    }
                }
                input.addTerminal(key, true, source);
            } else { // input terminal that receives data from only one output terminal
                input.addTerminal(key, false, param[key]["id"] + param[key]["output"]);

                if (param[key]["server"] != null) { // input temrminal that receives data from output terminal of server widget
                    subscribeToServerWidget(key, param[key]["id"] + param[key]["output"], param[key]["server"]["eventId"], param[key]["server"]["outputRpc"]);
                }
            }
        }
    });

    /**
     * Called when the concerning widget receives data from other widgets.
     */
    comm.register("receive", function (param) {
        input.receiveData(param["input"], param["id"] + param["output"], param["data"]);
        runIfPossible();
    });

    comm.register("output", function () {
        return {"output": output};
    });

    comm.register("clearInputData", function () {
        input.clearInputData();
    });

    comm.register("runIfPossible", function () {
        runIfPossible();
    });

    comm.register("getRunningInfo", function () {
        return {"startRunningTime": startRunningTime, "endRunningTime": endRunningTime, "hashValue": hashValue};
    });

    function subscribeToServerWidget(iTerminal, source, eventId, outputRpc) {
        function getOutputAndSubscribe() {
            bahn.call(outputRpc, eventId, function (data) {
                if (data != null && data != "null") {
                    input.receiveData(iTerminal, source, JSON.parse(data));
                    runIfPossible();
                }
            });

            bahn.subscribe(eventId, function (data) {
                input.receiveData(iTerminal, source, JSON.parse(data));
                runIfPossible();
            });
        }

        if (!bahn.isOpen()) {
            bahn.open(function () {
                getOutputAndSubscribe();
            });
        } else {
            getOutputAndSubscribe();
        }

    }

    function runIfPossible() {
        if (input.isAllDataReceived()) {
            startRunningTime = Date();
            var data = self.run(input.getData());
            if (data != null) {
                self.returnOutput(data);
            }
        }
    }
}
