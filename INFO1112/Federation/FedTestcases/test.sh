port1=1234
port2=4400
port3=2344

echo "[]" > "testcase_results.json"


echo TESTING FED_out_channels

newFile1="generated_FED_out_channels1.out"
newFile2="generated_FED_out_channels2.out"
coverage run -a ../server.py $port2 &
sleep 1s
while read -r line; do echo "$line"; sleep 0.1; done < "FED_out_channels1.in" | python3 ../client.py $port2 > $newFile1
coverage run -a ../server.py $port1 ../configuration.txt &
sleep 1s
while read -r line; do echo "$line"; sleep 0.1; done < "FED_out_channels2.in" | python3 ../client.py $port1 > $newFile2
sleep 1s
kill -INT `ps -ef | grep server.py | grep -v grep | awk '{print $2}'`
sleep 1s
if [[ "$(diff "$newFile1" "FED_out_channels1.out")" == "" ]] && [[ "$(diff "$newFile2" "FED_out_channels2.out")" == "" ]]; then
    python3 test.py "FED_out_channels" Passed
else
    diff $newFile1 "FED_out_channels1.out"
    diff $newFile2 "FED_out_channels2.out"
    python3 test.py "FED_out_channels" Failed
fi


echo TESTING SAY_channels

newFile11="generated_FED_SAY_channels1.out"
newFile21="generated_FED_SAY_channels2.out"
coverage run -a ../server.py $port2 &
sleep 1s
while read -r line; do echo "$line"; sleep 0.1; done < "FED_SAY_channels1.in" | python3 ../client.py $port2 > $newFile11 &
coverage run -a ../server.py $port1 ../configuration.txt &
sleep 1s
while read -r line; do echo "$line"; sleep 0.1; done < "FED_SAY_channels2.in" | python3 ../client.py $port1 > $newFile21
#while read -r line; do echo "$line"; sleep 0.1; done < "logout" | python3 ../client.py $port2 > $newFile1

kill -INT `ps -ef | grep server.py | grep -v grep | awk '{print $2}'`
kill -INT `ps -ef | grep client.py | grep -v grep | awk '{print $2}'`
sleep 1s

if [[ "$(diff "$newFile11" "FED_SAY_channels1.out")" == "" ]] && [[ "$(diff "$newFile21" "FED_SAY_channels2.out")" == "" ]]; then
    python3 test.py "FED_SAY_channels" Passed
else
    diff $newFile11 "FED_SAY_channels1.out"
    diff $newFile21 "FED_SAY_channels1.out"
    python3 test.py "FED_SAY_channels" Failed
fi


echo TESTING SAY_transfer

newFile12="generated_FED_SAY_transfer1.out"
newFile22="generated_FED_SAY_transfer2.out"
coverage run -a ../server.py $port3 &
sleep 1s
coverage run -a ../server.py $port2 &
sleep 1s
coverage run -a ../server.py $port1 ../configuration.txt &
sleep 1s
while read -r line; do echo "$line"; sleep 0.1; done < "FED_SAY_transfer1.in" | python3 ../client.py $port2 > $newFile12
#create channel in port2 for user to join
sleep 1s
while read -r line; do echo "$line"; sleep 0.1; done < "FED_SAY_transfer2.in" | python3 ../client.py $port3 > $newFile22
#while read -r line; do echo "$line"; sleep 0.1; done < "logout" | python3 ../client.py $port2 > $newFile1
sleep 1s
kill -INT `ps -ef | grep server.py | grep -v grep | awk '{print $2}'`
kill -INT `ps -ef | grep client.py | grep -v grep | awk '{print $2}'`
sleep 1s

if [[ "$(diff "$newFile12" "FED_SAY_transfer.out")" == "" ]] && [[ "$(diff "$newFile22" "FED_SAY_transfer.out")" == "" ]]; then
    python3 test.py "SAY_channels" Passed
else
    diff $newFile11 "FED_SAY_transfer1.out"
    diff $newFile21 "FED_SAY_transfer2.out"
    python3 test.py "FED_SAY_channels" Failed
fi


list_of_basic_testcase=(
    "multiple_channels_output"
    "valid_create_join"
    "valid_login_register"
    "valid_say_recv"
    "invalid_command"
    "invalid_create"
    "invalid_join"
    "invalid_say_recv"
    "invalid_login_register"
)


for x in ${list_of_basic_testcase[*]}
do
    for file in *
    do
        if [[ $x.in == $file ]]
        then
            echo TESTING $file
            newFile="generated_$x.out"
            coverage run -a ../server.py $port1 &

            sleep 1s
            while read -r line; do echo "$line"; sleep 0.1; done < $file | python3 ../client.py $port1 > $newFile
            kill -INT `ps -ef | grep server.py | grep -v grep | awk '{print $2}'`
            sleep 1s

            if [[ "$(diff "$newFile" "$x".out)" == "" ]]; then
                python3 test.py $x Passed
            else
                diff $newFile $x.out
                python3 test.py $x Failed
            fi

        fi
    done
done

rm ./generated_*


coverage report -m
echo FOR MORE INFORMATION ON COVERAGE GO TO:
coverage html

echo TESTCASE RESULTS
cat testcase_results.json

