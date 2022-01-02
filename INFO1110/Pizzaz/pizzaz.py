# Name: Zhengpu Hu
# unikey: zhhu3786
import sys
from decimal import Decimal, ROUND_UP

# --------------------------------------------------------------------------------------------------------------------
# Saved data for later use
# movie_list[movienumber][2]=time_reference,[3]=room_reference
movie_list = [ \
    ["The Shining. 1980. 2h 26m. 10:00. Room 1", 600, 1,18], \
    ["Your Name. 2016. 1h 52m. 13:00. Room 1", 780, 1,18], \
    ["Fate/Stay Night: Heaven's Feel - III. Spring Song. 2020. 2h 0m. 15:00. Room 1", 900, 1,18], \
    ["The Night Is Short, Walk on Girl. 2017. 1h 32m. 17:30. Room 1", 1050, 1,18], \
    ["The Truman Show. 1998. 1h 47m. 19:30. Room 1", 1170, 1,18], \
    ["Genocidal Organ. 2017. 1hr 55m. 21:45. Room 1", 1305, 1,18], \
    ["Jacob's Ladder. 1990. 1h 56m. 10:00. Room 2", 600, 2,68], \
    ["Parasite. 2019. 2h 12m. 12:15. Room 2", 735, 2,68], \
    ["The Dark Knight. 2008. 2h 32min. 14:45. Room 2", 885, 2,68], \
    ["Blade Runner 2049. 2017. 2h 44m. 17:45. Room 2", 1065, 2,68], \
    ["The Mist. 2007. 2h 6m. 21:00. Room 2", 1260, 2,68], \
    ["Demon Slayer: Mugen Train. 2020. 1h59min. 23:20. Room 2", 1400, 2,68], \
    ["The Matrix. 1999. 2h 16m. 10:00. Room 3", 600, 3,21], \
    ["Inception. 2010. 2h 42m. 11:30. Room 3", 690, 3,21], \
    ["Shutter Island. 2010. 2h 19m. 14:30. Room 3", 870, 3,21], \
    ["Soul. 2020. 1hr 40m. 17:00. Room 3", 1020, 3,21], \
    ["Mrs. Brown. 1997. 1h 41min. 19:00. Room 3", 1140, 3,21], \
    ["Peppa Pig: Festival of Fun. 2019. 1h 8min. 21:00. Room 3", 1260, 3,21], \
    ["Titanic. 1997. 3h 30min. 22:15. Room 3", 1334, 3,21]]

# Receipt detail
popcorn_size = ("")
movie_for_single = ("")
seat_number_single = 17

people_number = ("")
movie_number_for_group = ("")
# --------------------------------------------------------------------------------------------------------------------
def bye():
    print()
    print("Bye.")
    sys.exit()


def check_time_and_convert():
    try:
        time = sys.argv[2]
    except IndexError:
        print("Sorry. This program does not recognise the switch options. ")
        bye()
    timedigit = time.replace(":", "")

    if len(time) == 5 and timedigit.isdigit():
        timenow = sys.argv[2]
        timenow = timenow.replace(":", "")
        timenow_check_hour = (int(timenow) // 100)
        timenow_check_minute = (int(timenow) % 100)

        if timenow_check_hour < 24 and timenow_check_minute < 60:
            timenow = ((int(timenow) // 100) * 60) + (int(timenow) % 100)
            return timenow
        else:
            print("Sorry. This program does not recognise the time format entered.")
            bye()
    else:
        print("Sorry. This program does not recognise the time format entered.")
        bye()




def book():
    def find_movie():
        movie_want_to_watch = input("What is the name of the movie you want to watch?")
        i = 0
        while i <= 18 and movie_want_to_watch != movie_list[i][0]:
            i += 1
        if i > 18:
            answer = input("Sorry, we could not find that movie. Enter Y to try again or N to quit.")
            while answer != "Y" and answer != "N":
                input("Sorry, we could not find that movie. Enter Y to try again or N to quit.")
                find_movie()
            if answer == "Y":
                find_movie()
            elif answer == "N":
                bye()
        return i

    def popcorn_order():
        answer = input("Would you like to order popcorn? Y/N")
        if answer == "Y":
            answer = input("You want popcorn. What size Small, Medium or Large? (S/M/L)")
            while answer != "S" and answer != "M" and answer != "L":
                answer = input("You want popcorn. What size Small, Medium or Large? (S/M/L)")
            if answer == "S":
                single_order_list.append("Small popcorn")
                single_order_list.append("#17")
            elif answer == "M":
                single_order_list.append("Medium popcorn")
                single_order_list.append("#17")
            elif answer == "L":
                single_order_list.append("Large popcorn")
                single_order_list.append("#17")
        elif answer == "N":
            single_order_list.append("none")
            single_order_list.append("#17")
        else:
            popcorn_order()
        return single_order_list

    def receipt_printer_single():
        movie_time = movie_list[movie_number][1]
        if movie_time <= 960:
            single_order_list.append(13.00)
            time = "before 16:00"
        else:
            single_order_list.append(15.00)
            time = "after 16:00"

        if single_order_list[1] == "Small popcorn":
            single_order_list.append(3.50)
        elif single_order_list[1] == "Medium popcorn":
            single_order_list.append(5.00)
        elif single_order_list[1] == "Large popcorn":
            single_order_list.append(7.00)
        elif single_order_list[1] == "none":
            single_order_list.append(0.00)

        initial_cost = single_order_list[3] +single_order_list[4]
        ticket_cost = single_order_list[3]
        popcorn_size = single_order_list[1]
        popcorn_cost = single_order_list[4]
        discount = float(0.00)
        final_cost = initial_cost - discount

        if single_order_list[1] != "none":
            print("For 1 person, the initial cost is \t${:.2f}\n Person 1: Ticket {}\t\t${:.2f}\n Person 1: {} \t\t$ {:.2f}\n\n No discounts applied \t\t\t$ {:.2f}\n\nThe final price is  \t\t\t${:.2f}".format(initial_cost,time,ticket_cost,popcorn_size,popcorn_cost,discount, final_cost))
        else:
            print("For 1 person, the initial cost is \t${:.2f}\n Person 1: Ticket {}\t\t${:.2f}\n \n No discounts applied \t\t\t$ {:.2f}\n\nThe final price is  \t\t\t${:.2f}".format(initial_cost,time,ticket_cost,discount, final_cost))

    single_order_list = [1]
    movie_number = find_movie()
    popcorn_order()
    print(single_order_list)
    receipt_printer_single()


def group_booking():
    def find_movie():
        movie_want_to_watch = input("What is the name of the movie you want to watch?")
        i = 0
        while i <= 18 and movie_want_to_watch != movie_list[i][0]:
            i += 1
        if i > 18:
            answer = input("Sorry, we could not find that movie. Enter Y to try again or N to quit.")
            while answer != "Y" and answer != "N":
                input("Sorry, we could not find that movie. Enter Y to try again or N to quit.")
                find_movie()
            if answer == "Y":
                find_movie()
            elif answer == "N":
                bye()
        return i

    def people_number():
        num = int(input("How many persons will you like to book for?"))
        while num <= 1:
            answer = input(
                "Sorry, you must have at least two customers for a group booking. Enter Y to try again or N to quit.")
            while answer != "Y" and answer != "N":
                answer = input(
                    "Sorry, you must have at least two customers for a group booking. Enter Y to try again or N to quit.")
            if answer == "Y":
                num = int(input("How many persons will you like to book for?"))
            elif answer == "N":
                bye()
        if num > movie_list[movie_number_for_group][3]:
            answer = input("Sorry, we do not have enough space to hold {} people in the theater room of {} seats. Enter Y to try a different movie name or N to quit.".format(num, movie_list[movie_number_for_group][3]))
            while answer != "Y" and answer != "N":
                answer = input("Sorry, we do not have enough space to hold {} people in the theater room of {} seats. Enter Y to try a different movie name or N to quit.".format(num, movie_list[movie_number_for_group][3]))
            if answer == "Y":
                group_booking()
            elif answer == "N":
                bye()

        return num

    def group_popcorn_order_():
        group_order_list = []
        # Iterate over a sequence of numbers from 0 to 4
        for i in range(people_number):
            # In each iteration, add an empty list to the main list
            group_order_list.append([i+1])
        print(group_order_list)

        i =1
        while i <= people_number:
            answer = input("Would you like to order popcorn? Y/N")
            if answer == "Y":
                answer = input("You want popcorn. What size Small, Medium or Large? (S/M/L)")
                while answer != "S" and answer != "M" and answer != "L":
                    answer = input("You want popcorn. What size Small, Medium or Large? (S/M/L)")
                    continue
                if answer == "S":
                    group_order_list[i-1].append("Small popcorn")
                    group_order_list[i - 1].append("#"+str(2 * i - 1))
                elif answer == "M":
                    group_order_list[i-1].append("Medium popcorn")
                    group_order_list[i - 1].append("#"+str(2 * i - 1))
                elif answer == "L":
                    group_order_list[i-1].append("Large popcorn")
                    group_order_list[i - 1].append("#"+str(2 * i - 1))
                i +=1
            elif answer == "N":
                group_order_list[i - 1].append("none")
                group_order_list[i - 1].append("#"+str(2 * i - 1))
                i +=1

            else:
                group_popcorn_order_()

        return group_order_list

    def receipt_printer_group():
        movie_time = movie_list[movie_number_for_group][1]

        if movie_time <= 960:
            for order in group_order_list:
                # In each iteration, add an empty list to the main list
                order.append(13.00)
            time = "before 16:00"
        else:
            single_order_list.append(15.00)
            time = "after 16:00"

        i = 0
        while i <= people_number -1:
            if group_order_list[i][1] == "Small popcorn":
                group_order_list[i].append(3.50)
                i += 1
            elif group_order_list[i][1] == "Medium popcorn":
                group_order_list[i].append(5.00)
                i += 1
            elif group_order_list[i][1] == "Large popcorn":
                group_order_list[i].append(7.00)
                i += 1
            elif group_order_list[i][1] == "none":
                group_order_list[i].append(0.00)
                i += 1


        initial_ticket_cost = 0
        initial_popcorn_cost = 0
        i=0
        while i <= people_number -1:
            initial_ticket_cost += group_order_list[i][3]
            initial_popcorn_cost += group_order_list[i][4]
            i += 1
        if (initial_popcorn_cost + initial_ticket_cost) >= 100:
            discount = initial_popcorn_cost *0.1 +initial_ticket_cost*0.2

        final_cost = initial_ticket_cost + initial_popcorn_cost - discount


        return group_order_list

        # initial_cost = single_order_list[3] + single_order_list[4]
        # ticket_cost = single_order_list[3]
        # popcorn_size = single_order_list[1]
        # popcorn_cost = single_order_list[4]
        # discount = float(0.00)
        # final_cost = initial_cost - discount
        #
        # if single_order_list[1] != "none":
        #     print(
        #         "For 1 person, the initial cost is \t${}\n Person 1: Ticket {}\t\t${}\n Person 1: {} \t\t$ {}\n\n No discounts applied \t\t\t$ {}\n\nThe final price is  \t\t\t${}".format(
        #             initial_cost, time, ticket_cost, popcorn_size, popcorn_cost, discount, final_cost))
        # else:
        #     print(
        #         "For 1 person, the initial cost is \t${}\n Person 1: Ticket {}\t\t${}\n \n No discounts applied \t\t\t$ {}\n\nThe final price is  \t\t\t${}".format(
        #             initial_cost, time, ticket_cost, discount, final_cost))

    movie_number_for_group = int(find_movie())
    people_number = int(people_number())
    group_order_list = group_popcorn_order_()
    print(receipt_printer_group())




# --------------------------------------------------------------------------------------------------------------------
print("""-=-=-=-=-=-=-=-=-=-=-=-=-=-=
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
~ Welcome to Pizzaz cinema ~
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-=-=-=-=-=-=-=-=-=-=-=-=-=-=""")
print()

try:
    switch = sys.argv[1]
except IndexError:
    print("Usage: python3 pizzaz.py [--show <timenow> | --book | --group]")
    sys.exit()

if switch == "--show":
    timenow = check_time_and_convert()
    for movie in movie_list:
        if movie[1] >= timenow:
            print(movie[0])
    bye()


elif switch == "--book":
    print("book")
    book()




elif switch == "--group":
    print("group")
    print(group_booking())



else:
    print("Sorry. This program does not recognise the switch options. ")
    print()
    print("Bye.")