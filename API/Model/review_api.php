<?php
    class ModelCatalogReviewApi extends Model {

        //Function to register new customer
        public function register($username,$password,$fname,$lname,$telno){
            $query=$this->db->query("SELECT email FROM oc_customer WHERE email='".$username."'");
            if($query->num_rows)
                return array("reg"=>"fail","error"=>"E-mail Registered Already.");

            if($query=$this->db->query("INSERT INTO oc_customer SET customer_group_id =1, store_id = 0, language_id =1, firstname = '" . $this->db->escape($fname) . "',
            lastname = '" . $this->db->escape($lname) . "', email = '" . $this->db->escape($username) . "', telephone = '" . $this->db->escape($telno) . "',
             custom_field = '', salt = '" . $this->db->escape($salt = token(9)) . "', password = '" . $this->db->escape(sha1($salt . sha1($salt . sha1($password)))) . "',
              newsletter = 0, ip = '::1', status = 1, date_added = NOW()")){
                return array("reg"=>"success");
            }
            else{
                return array("reg"=>"fail","error"=>"Something Went Wrong. Please Try Again Later");
            }
        }

        //Function to get customer data
        public function getUserData($id){
            $query=$this->db->query("SELECT CONCAT(firstname,' ',lastname) AS name,email,telephone FROM oc_customer WHERE customer_id=".$id);
            if($query->num_rows){
                return $query->rows[0];
            }
            else{
                return array("auth"=>"fail","error"=>"Unable to Retrieve User Data.");
            }
        }

        //Function to get all restaurants
        public function getAllRestaurants(){
            $query=$this->db->query("SELECT * FROM restaurant");
            if($query->num_rows){
                $tmp['restaurants']=$query->rows;
                
                foreach($tmp['restaurants'] as &$restaurant){
                    $query=$this->db->query("SELECT COUNT(review_id) AS num_review,ROUND(AVG(rating),0) AS rating FROM reviews WHERE restaurant_id=".$restaurant['restaurant_id']);
                    if($query->num_rows){
                        $restaurant['num_review']=$query->rows[0]['num_review'];
                        if($restaurant['num_review']!=0)
                            $restaurant['rating']=$query->rows[0]['rating'];
                        else
                            $restaurant['rating']=0;
                    }
                    else{
                        $restaurant['num_review']=0;
                        $restaurant['rating']=0;
                    }
                }

                $tmp['status']="success";
                return $tmp;
            }
            else{
                return array("status"=>"fail","error"=>"no restaurants found.");
            }
        }

        //Function to get all featured restaurants
        public function getFeatured(){
            $query=$this->db->query("SELECT * FROM featured_restaurants WHERE CURDATE() BETWEEN start_date AND end_date");
            if($query->num_rows){
                $tmp['featured']=$query->rows;
                
                $count=0;
                foreach($tmp['featured'] as $restaurant){
                    if($count==0)
                        $ids=$restaurant['restaurant_id'];
                    else
                        $ids.=','.$restaurant['restaurant_id'];
                    $count++;
                }

                $featured_query=$this->db->query("SELECT * FROM restaurant WHERE restaurant_id IN(".$ids.")");
                if($featured_query->num_rows){
                    $tmp2['restaurants']=$featured_query->rows;

                    foreach($tmp2['restaurants'] as &$restaurant){
                        $query=$this->db->query("SELECT COUNT(review_id) AS num_review,ROUND(AVG(rating),0) AS rating FROM reviews WHERE restaurant_id=".$restaurant['restaurant_id']);
                        if($query->num_rows){
                            $restaurant['num_review']=$query->rows[0]['num_review'];
                            if($restaurant['num_review']!=0)
                                $restaurant['rating']=$query->rows[0]['rating'];
                            else
                                $restaurant['rating']=0;
                        }
                        else{
                            $restaurant['num_review']=0;
                            $restaurant['rating']=0;
                        }
                    }

                    $tmp2['status']="success";
                }
                else{
                    return array("status"=>"fail","error"=>"no restaurants found.");
                }

                return $tmp2;
            }
            else{
                return array("status"=>"fail","error"=>"no restaurants found.");
            }
        }
        
        //Function to get dishes
        public function getDishes($id){
            $query=$this->db->query("SELECT * FROM dishes WHERE restaurant_id=".$id);
            if($query->num_rows){
                $tmp['dishes']=$query->rows;

                foreach($tmp['dishes'] as &$dish){
                    $query=$this->db->query("SELECT COUNT(dish_review_id) AS num_review,ROUND(AVG(rating),0) AS rating FROM dish_review WHERE dish_id=".$dish['dish_id']);
                    if($query->num_rows){
                        $dish['num_review']=$query->rows[0]['num_review'];
                        if($dish['num_review']!=0)
                            $dish['rating']=$query->rows[0]['rating'];
                        else
                            $dish['rating']=0;
                    }
                    else{
                        $dish['num_review']=0;
                        $dish['rating']=0;
                    }
                }

                $tmp['status']="success";
                return $tmp;
            }
            else{
                return array("status"=>"fail","error"=>"No Dishes Found!");
            }
        }

        //Function to get restaurant rating
        public function getRestaurantRating($id){
            $query=$this->db->query("SELECT COUNT(review_id) AS num_review,ROUND(AVG(rating),0) AS rating FROM reviews WHERE restaurant_id=".$id);
            if($query->num_rows){
                $restaurant['num_review']=$query->rows[0]['num_review'];
                if($restaurant['num_review']!=0)
                    $restaurant['rating']=$query->rows[0]['rating'];
                else
                    $restaurant['rating']=0;
            }
            else{
                $restaurant['num_review']=0;
                $restaurant['rating']=0;
            }

            return $restaurant;
        }

        //Function to give restaurant review
        public function giveRestaurantReview($restaurant_id,$customer_id,$rating,$review){
            $query=$this->db->query("INSERT INTO reviews(restaurant_id,customer_id,rating,review,date_added) VALUES(".$restaurant_id.",".$customer_id.",".$rating.",'".$review."',CURDATE())");
            if($query){
                return array("status"=>"success");
            }
            else{
                return array("status"=>"fail","error"=>"Unable to Give Review.");
            }
        }

        //Function to get restaurant review
        public function getRestaurantReview($id){
            $query=$this->db->query("SELECT review_id,customer_id,rating,review,date_added FROM reviews WHERE restaurant_id=".$id." ORDER BY date_added DESC");
            if($query->num_rows){
                $tmp['reviews']=$query->rows;

                foreach($tmp['reviews'] as &$review){
                    $query=$this->db->query("SELECT CONCAT(firstname,' ',lastname) AS name FROM oc_customer WHERE customer_id=".$review['customer_id']);
                    if($query->num_rows){
                        $review['name']=$query->rows[0]['name'];
                        unset($review['customer_id']);
                    }
                }

                $tmp['status']="success";
                return $tmp;
            }
            else{
                $tmp['status']="fail";
                $tmp['error']="No Reviews Found!";
            }
        }

        //Function to give dish review
        public function giveDishReview($dish_id,$customer_id,$rating,$review){
            $query=$this->db->query("INSERT INTO dish_review(dish_id,customer_id,rating,review,date_added) VALUES(".$dish_id.",".$customer_id.",".$rating.",'".$review."',CURDATE())");
            if($query){
                return array("status"=>"success");
            }
            else{
                return array("status"=>"fail","error"=>"Unable to Give Review.");
            }
        }

        //Function to get dish review
        public function getDishReview($id){
            $query=$this->db->query("SELECT dish_review_id AS review_id,customer_id,rating,review,date_added FROM dish_review WHERE dish_id=".$id." ORDER BY date_added DESC");
            if($query->num_rows){
                $tmp['reviews']=$query->rows;

                foreach($tmp['reviews'] as &$review){
                    $query=$this->db->query("SELECT CONCAT(firstname,' ',lastname) AS name FROM oc_customer WHERE customer_id=".$review['customer_id']);
                    if($query->num_rows){
                        $review['name']=$query->rows[0]['name'];
                        unset($review['customer_id']);
                    }
                }

                $tmp['status']="success";
                return $tmp;
            }
            else{
                $tmp['status']="fail";
                $tmp['error']="No Reviews Found!";
            }
        }

        //Function to get dish rating
        public function getDishRating($id){
            $query=$this->db->query("SELECT COUNT(dish_review_id) AS num_review,ROUND(AVG(rating),0) AS rating FROM dish_review WHERE dish_id=".$id);
            if($query->num_rows){
                $tmp=$query->rows[0];
                $tmp['status']="success";
            }
            else{
                $tmp['status']="fail";
                $tmp['error']="Unable to get dish rating";
            }
            return $tmp;
        }
    }
?>