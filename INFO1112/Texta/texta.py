
if __name__ == '__main__':
    import sys
    import re

    def read_file(address):
        # this function will validate the existence and readability of input address
        # through parameter and convert command file to a 2-D list.
        try:
            with open(address) as f:
                file = f.read().splitlines()
            # open argument 1 and store it in file, close it after storing.
            if address == sys.argv[1]:
                # if address is command file then this if statement will remove all
                # comments and store it in 2-D list
                command_file = []
                if len(file)<1:
                    raise FileNotFoundError("")
                i = 0
                while i < len(file):
                    # this while loop will remove all comment and contain command in a 2-D
                    # list with command in index 0 for each list.
                    # note that only text after hashtag with even number of double
                    # quote in front will be count as a comment, this allow # to exists
                    # as string if it enclosed by double-quote.
                    j = 0
                    dc_count = 0
                    command = file[i]
                    while j < len(file[i]):
                    # this while loop will delete all comment in command
                        if file[i][j] == "\"":
                            dc_count += 1
# T21 cover
                        if file[i][j] == "#" and dc_count % 2 == 0 and dc_count != 1:
                            command = file[i][0:j]
                            break
                        # this if statement will only remove comment by identifying
                        # number of doublequote before the hashtag
# T21 cover
                        j += 1
                    if "filter" in command or "fields" in command or "replace" in command:
                        head, sep, tail = command.partition(" ")
                        command_file.append([head, tail])
                    else:
                        command_file.append(command.split())

                    i += 1
                # at this point the command_file is the command file we are going to use
                return command_file
            else:
                return file
        # if the address is not sys.argv[1](not command file), the function will read
        # address and return file directly.

        except FileNotFoundError:
            # will enter this excpetion lock if address file does not exists
            # or not readable.
            sys.stderr.write("Error: file {} not readable".format(address))
            sys.stderr.write("\n")
            # will print put error message to stderr
            if address == sys.argv[1]:

                sys.exit()
            # if command file is not readable, there is no need to continue excecute the
            # rest of the code.
# T1 cover.
            else:
                return "not valid"
# T2 cover
            # the function will return string "not valid" as file to signifies that the
            # file is not readable and need to be ignored

    def command_file_check(command_file):
        # this function will verifies the validity of each command in given parameter
        # file, will print error message to stderr and append valid command index into
        # a new list
        valid_command_index= []
        i = 0
        while i< len(command_file):
            # this while loop will check all syntax of all command in command_file, \
            # will print error mesage to stderr. And will store valid command index
            # into "valid_command_index" list for furture usage.
            if len(command_file[i]) == 1 and command_file[i][0] != "count":
                if 'filter' == command_file[i][0] or 'fields' == command_file[i][
                0] or 'replace' == command_file[i][0]:
                    sys.stderr.write("Error: command line {}: no argument "
                                     "provided".format(i + 1))
                    sys.stderr.write("\n")
# T16 cover
            # this if block will check if command(except count) is a valid command
            # and check if there is no argument provided to print out error message
            # "no argument provided"

            elif len(command_file[i]) <1:
                sys.stderr.write("Error: command line {}: not a valid command".format(i
                                                                                     + 1))
                sys.stderr.write("\n")
            # This if statment will check if this file line is not an empty line
# T4 cover
# T5 cover
            # note that line contain comment onlt will coutns as empty
# T15 cover
            elif command_file[i][0] == 'filter':
                # any string after filter will be treat as one single argument,
                # if the argument contains space but not enclosed by doublequote, \
                #         an re.error will be raise.
                try:
                    command_file[i][1] = command_file[i][1].strip()
                    re.compile(command_file[i][1])
# T19 cover
                    if command_file[i][1][0] != "\"" or command_file[i][1][
                            -1]!= "\"":
                        # this if statement will check if the argument contains space
                        # is enclosed by doublequote.
                            raise re.error("")
# T7 cover
                    if command_file[i][1].count("\"")>2:
                        # this if statment will check if there is more than two
                        # doublequote in argument, will raise re.error if there is more
                        # than 2 as regexp and string cannot contains doublequote
                            raise re.error("")
# T13 cover
# T7 cover
                    else:
                        if command_file[i][1][0] == "\"" and command_file[i][1][
                            -1]!= "\"":
                        # this if statement will check if the argument by only one
                        # double-quote
                            raise re.error("")
# T6 cover
                        elif command_file[i][1][0] != "\"" and command_file[i][1][
                            -1]== "\"":
                        # this if statement will check if the argument is enclosed by
                        # only one double-quote
                            raise re.error("")
# T6 cover
                    valid_command_index.append(i)
# T19 cover
                except re.error:
                    sys.stderr.write("Error: command line {}: bad regexp {} "
                                     "argument".format(i + 1, command_file[i][1]))
                    sys.stderr.write("\n")
# T6,T7 cover

            elif command_file[i][0] == 'fields':
                # there should not be doublequote in index(field number), if so the
                # doublequote will be seen as doublequote that used to enclose field
                # delimiter
                command_file[i][1] = command_file[i][1].strip()
                if len(command_file[i][1].strip().split("\"")) == 3:
                    delimiter = command_file[i][1].split("\"")[1]
                    index = command_file[i][1].strip().split("\"")[2].strip().split(" ")
                # if there are only two doublequote in delimiter, the whole argument
                # should be separate into three item.
                    command_file[i][1] = delimiter
                    command_file[i] = command_file[i]+index
                    valid_command_index.append(i)

# T22 cover
                else:
                    sys.stderr.write("Error: command line {}: bad field delimiter"
                                    "".format(i + 1))
                    sys.stderr.write("\n")
# T8 cover

            elif command_file[i][0] == 'replace':
                # split string by double-quote, there should be five item in command[
                # i][1] after split ['', 'string1', ' ', 'string2', '']
                if len(command_file[i][1].split("\"")) == 5 and "\\n" not in command_file[i][1]:
# string to replace or to be replaced cannot contains newline character
                    command_file[i][1] = \
                    command_file[i][1].split("\"")
                    # this if block will check if list has exactly 5 item
                    valid_command_index.append(i)
                else:
                    sys.stderr.write(
                "Error: command line {}: incorrect string in replace"
                    .format(i + 1))
                    sys.stderr.write("\n")
# T10 cover
# T11 cover
# T17 cover
            elif command_file[i][0] == 'count':
                if len(command_file[i]) != 1:
                    sys.stderr.write(
                        "Error: command line {}: invalid command"
                            .format(i + 1))
                    sys.stderr.write("\n")
                # this if block will check if there is any other argument after command
# T12 cover
                else:
                    valid_command_index.append(i)

            else:
                sys.stderr.write("command line {}: invalid command".format(i + 1))
                sys.stderr.write("\n")
# T18 cover
            i += 1

        return valid_command_index

    def command_excecute(command_file,valid_command_index,file):
        # This function will excecute each commad according to valid_command_index to
        # each line in file.
        count_command = False
        count_printed_line = 0
        i = 0
        while i < len(file):
            skip = False
        # this while loop will iterates n times, n is number of lines in file
            j = 0
            while j < len(valid_command_index):
                if command_file[valid_command_index[j]][0] == 'filter':

                    if not bool(re.search(command_file[valid_command_index[j]][
                        1].strip("\""),file[i])):
# T19 cover
                        skip = True
                        # file[i] will pass into this if statememt if it does not follow
                        # the regex argument
                    j += 1

                elif command_file[valid_command_index[j]][0] == 'fields' and not skip:
                    delimiter = command_file[valid_command_index[j]][1]
# T9 cover
                    if not delimiter:
                        delimiter = " "
                    # if delimiter is "", strip "" will cause string to become boolean
                    # false, which will fall into below if statement to assign blank
                    # space to delimiter
                    temp = file[i].split(delimiter)
                    temp_arranged = []
                    # spearate the string according to delimiter
                    # print(231,temp)
                    k = 2
                    if len(temp)>0 and len(file[i]) >0:
                        while k < len(command_file[valid_command_index[j]]):
                            if command_file[valid_command_index[j]][k].isdigit():
                                if int(command_file[valid_command_index[j]][k]) >= len(temp):
                                    #   the line will fall into this if statement if
                                    #   the delimiter
                                    #   does not apply to this line or the index is too
                                    #   big for
                                    #   this line's elements.
                                    sys.stderr.write("Error: command line {}: bad field "
                                                     "number".format(j+1))
                                    sys.stderr.write("\n")
                                    k+=1
                                    break

# T9 cover
                                if len(temp_arranged) != 0:
                                    temp_arranged.append(delimiter)
                                # this code will add delimiter in front of element if
                                # element is not first element of list.
                                temp_arranged.append(
                                    temp[int(command_file[valid_command_index
                                    [j]][k])])
# T22 cover
                                # this append will append index in field corresponding to
                                # temp's index to temp_arranged list
                                k += 1
                                file[i] = ''.join(temp_arranged)
                            # T9 cover
                            #       this if statement will rewrite temp_arranged back to file
                            #       only if all index of field is valid (checked by
                            #       previous if)
                            else:
                                sys.stderr.write("Error: line {}: bad field number {}"
                                                 "".format(i + 1, command_file[
                                    valid_command_index[j]][k]))
                                # T9 cover
                                sys.stderr.write("\n")
                                break
                                # k += 1
                    else:
                        skip = True
                    j += 1
                elif command_file[valid_command_index[j]][0] == 'replace':


                    file[i] = file[i].replace(command_file[valid_command_index[
                        j]][1][1].strip("\""),command_file[valid_command_index[
                        j]][1][3].strip("\""))
                    j+=1
# T20 cover
                elif command_file[valid_command_index[j]][0] == 'count':
                    count_command = True
                    j+=1

                else:
                    j+=1
            if not skip:
                sys.stdout.write(file[i])
                sys.stdout.write('\n')
                count_printed_line +=1
# T19 cover
            i+=1
        if count_command:
            sys.stderr.write(str(count_printed_line))
            sys.stderr.write('\n')
# T21 cover
        return
    command_file = read_file(sys.argv[1])
    valid_command_index = command_file_check(command_file)

    if len(sys.argv) ==2:
        file = sys.stdin.readlines()
        command_excecute(command_file, valid_command_index, file)
    # the texta will read from standard input if there is no given input file (
    # sys.argv) has length 2
# T14 cover
    else:
    # the texta will read address given from system argument and pass it to
    # excecute_command according to argument order.
        i = 2
        while i< len(sys.argv):
            file = read_file(sys.argv[i])
            if file == "not valid":
# if the current text file not readable, the program will go on to next file(after
# print out error message)
# T3 cover
                i+=1
                continue
            command_excecute(command_file, valid_command_index, file)
            i+=1