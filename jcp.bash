jcp() {
    java -cp bin Main Lab1.map $1 $2 1
}

clean() {
    git clean -df
}

copyJava() {
    cp ../all_submissions_pcp-lp1-24_Trainspotting/group$1/submission01/Lab1.java src
}

copyMap() {
    cp ../all_submissions_pcp-lp1-24_Trainspotting/group$1/submission01/Lab1.map .
}

copyPdf() {
    cp ../all_submissions_pcp-lp1-24_Trainspotting/group$1/submission01/*.pdf .
}

copyAll() {
    copyJava $1 && copyMap $1 && copyPdf $1
}

rmCopy() {
    clean
    copyAll $1
}

run() {
    make
    jcp 5 15 & jcp 5 1 & jcp 7 1 & jcp 5 10 & jcp 2 5 & jcp 1 10 & jcp 4 2 & jcp 7 14
}