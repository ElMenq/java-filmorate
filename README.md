# java-filmorate
Template repository for Filmorate project.
## sprint 11
### Схема базы данных
![Без имени](https://github.com/ElMenq/java-filmorate/assets/149114821/172423b7-3472-49df-8eaf-95de432f4286)

## Примеры SQL запросов:

1. * `Получаем информацию по пользователю с id=1`
```SQL
  SELECT *
  FROM USER
  WHERE user_id = 1*
```  
  
2. * `Узнаем, кто поставил лайки из друзей фильму с id=10`
```SQL
  SELECT user_id
  FROM likes
  WHERE film_id = 10
  ```  
  
3. * `Получаем все фильмы с рейтингом 'PG'`
  ```SQL
  SELECT f.name
  FROM film AS f
  INNER JOIN rating AS r ON f.rating_ID = r.rating_ID
  WHERE r.name = 'PG'*
``` 


