var BASE64_CHARS='ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/'

function trim(s) {
    //remove all blank.
    var res = '';
    for(var i = 0; i<s.length; i++) {
        var c = s.charAt(i);
        if ( c!=' ' && c !='\n' && c !=':' && c !='\t') res += c;
    }
    return res;
}

/////////////////////////////String - B64///////////////////////////////////////////
function stringToB64(t) {
    var a, c, n;
    var r='', l=0, s=0;
    var tl=t.length;

    for(n=0; n<tl; n++) {
        c=t.charCodeAt(n);
        if(s == 0) {
            r+=BASE64_CHARS.charAt((c>>2)&63);
            a=(c&3)<<4;
        } else if(s==1) {
            r+=BASE64_CHARS.charAt((a|(c>>4)&15));
            a=(c&15)<<2;
        } else if(s==2) {
            r+=BASE64_CHARS.charAt(a|((c>>6)&3));
            l+=1;
            //if((l%60)==0) r+="\n";
            r+=BASE64_CHARS.charAt(c&63);
        }
        l+=1;
        //if((l%60)==0) r+="\n";
        s+=1;
        if(s==3) s=0;
    }
    if(s>0) {
        r+=BASE64_CHARS.charAt(a);
        l+=1;
        //if((l%60)==0) r+="\n";
        r+='=';
        l+=1;
        //if((l%60)==0) r+="\n";
    }
    if(s==1) r+='=';
    return r;
}

function b64ToString(base) {
    //remove all blank.
    var t = trim(base);

    var c, n;
    var r='', s=0, a=0;
    var tl=t.length;

    for(n=0; n<tl; n++) {
        c=BASE64_CHARS.indexOf(t.charAt(n));
        if(c >= 0) {
            if(s) r+=String.fromCharCode(a | (c>>(6-s))&255);
            s=(s+2)&7;
            a=(c<<s)&255;
        }
    }
    return r;
}

/////////////////////////////String - HEXA///////////////////////////////////////////


function stringToHexa(s) {
    var result = '';
    for(var i=0; i<s.length; i++) {
        c = s.charCodeAt(i);
        result += ((c<16) ? "0" : "") + c.toString(16);
    }
    return result;
}

function hexaToString(hexa) {
    //remove all blank.
    var hex = trim(hexa);

    var r='';
    if(hex.indexOf("#") == 0) hex = hex.substr(1);
    else if(hex.indexOf("0x") == 0 || hex.indexOf("0X") == 0) hex = hex.substr(2);

    if(hex.length%2) hex+='0';

    for(var i = 0; i<hex.length; i += 2)
        r += String.fromCharCode(parseInt(hex.slice(i, i+2), 16));
    return r;
}

//////////////////////////////HEXA -- B64 ////////////////////////////////////////

function hexaToB64(hexa) {
    var s =	hexaToString(hexa);
    return stringToB64(s);
}

function b64ToHexa(base) {
    var s =	b64ToString(base);
    return stringToHexa(s);
}