import json
import sys

# Define a function to create the mockSubject string
def create_mock_subject(name, goals, stats):
    goals_str = f"List.of({', '.join(map(lambda x: str(x)+'F', goals))})"
    stats_str = f"List.of({', '.join(map(lambda x: str(x)+'F', stats))})"
    return f"subjects.add(mockSubject(\"{name}\", {goals_str}, {stats_str}));\n"

# Read JSON data from either file or console input
try:
    with open('subjects.json') as f:
        json_data = json.load(f)
except FileNotFoundError:
    json_data = json.loads(input("Enter JSON data: "))

# Iterate through each subject
for subject in json_data:
    # Extract relevant data
    name = subject['name']
    weekly_goals = subject['weeklyGoals']
    daily_statistic = subject['dailyStatistic']

    # Extract values or default to 0 if not present
    weekly_hours = weekly_goals.get('hours', 0)
    weekly_questions = weekly_goals.get('questions', 0)
    weekly_reviews = weekly_goals.get('reviews', 0)
    daily_hours = daily_statistic.get('hours', 0)
    daily_questions = daily_statistic.get('questions', 0)
    daily_reviews = daily_statistic.get('reviews', 0)

    # Create the mockSubject string
    goals = [weekly_hours, weekly_questions, weekly_reviews]
    stats = [daily_hours, daily_questions, daily_reviews]
    mock_subject = create_mock_subject(name, goals, stats)

    # Print the mockSubject string
    print(mock_subject, end="")
