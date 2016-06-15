#!/usr/bin/awk -f

BEGIN {
    STREAM=1;
    FS=OFS=SUBSEP=",";
    ORS=""
}  
{
    gsub(" ", "", $3);
    gsub(" ", "", $2);
    if ($3 == "open") {
        last_open[$1] = $2;  
    } else if (last_open[$1] != "") {
        count[$1] += 1;
        sum[$1] += $2 - last_open[$1];
    }
}
END {
    j=1;    
    print "["
    for(i in sum) {
        printf "{%s,%s}",i,sum[i]/count[i]
        print (length(sum)==j?"]":",")
        j+=1;
    }
    print "\n"
}