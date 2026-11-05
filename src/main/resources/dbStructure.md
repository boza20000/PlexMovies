```mermaid
erDiagram
	direction TB
    media_item {
        long id PK ">=1L"  
		string title  "INDEX"  
		string description  ""  
		double rating  ">=0.0 AND <=10.0"  
		date release_date  "NOT NULL"  
		string imdb_id  "UNIQUE, NOT NULL, INDEX"  
    }

	movie {
        long media_item_id PK
        int duration_min  ">=1"  
	}

	series {
        long media_item_id PK
    }

	season {
		long id PK ">=1L,INDEX"  
		long series_id FK "NOT NULL, INDEX"  
		int season_number  "NOT NULL, >=1, UNIQUE(series_id, season_number)"  
		date release_date  ""  
	}

    episode {
		long media_item_id PK ">=1L, INDEX"  
		long season_id FK "NOT NULL, INDEX"  
		int episode_number  "NOT NULL, >=1, UNIQUE(season_id, episode_number)"  
		int duration_min  ">=1"  
	}

	award_media_item {
        long media_item_id PK, FK
		long award_id PK,FK    
	}

	genre_media_item {
        long media_item_id PK, FK
		long genre_id PK,FK   
	}

	languag_media_item {
        long media_item_id PK, FK
		long language_id PK,FK  
		long stream_type_id PK,FK    
	}

    people_media_item{
        long person_id PK, FK
        long media_item_id PK, FK
        long role_id PK, FK
    }

    person {
		long id PK ""  
		string name  ""  
	}

	role {
		long id PK ""  
		string role  ""  
	}

	genre {
		long id PK ""  
		string genre  ""  
    }

	language {
		long id PK ""  
		string language  ""  
	}

	stream_type {
		long id PK ""  
		string stream_type  ""  
	}

	award {
		long id PK ""  
		string name  ""  
	}

    SEASON}|--||SERIES:"N:1"
    EPISODE}|--||SEASON:"N:1"
