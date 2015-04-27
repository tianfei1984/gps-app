package cn.com.gps169.db.model;

import java.util.ArrayList;
import java.util.List;

public class TerminalVehicleExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public TerminalVehicleExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andTerminalVehiclesIdIsNull() {
            addCriterion("terminal_vehicles_id is null");
            return (Criteria) this;
        }

        public Criteria andTerminalVehiclesIdIsNotNull() {
            addCriterion("terminal_vehicles_id is not null");
            return (Criteria) this;
        }

        public Criteria andTerminalVehiclesIdEqualTo(Integer value) {
            addCriterion("terminal_vehicles_id =", value, "terminalVehiclesId");
            return (Criteria) this;
        }

        public Criteria andTerminalVehiclesIdNotEqualTo(Integer value) {
            addCriterion("terminal_vehicles_id <>", value, "terminalVehiclesId");
            return (Criteria) this;
        }

        public Criteria andTerminalVehiclesIdGreaterThan(Integer value) {
            addCriterion("terminal_vehicles_id >", value, "terminalVehiclesId");
            return (Criteria) this;
        }

        public Criteria andTerminalVehiclesIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("terminal_vehicles_id >=", value, "terminalVehiclesId");
            return (Criteria) this;
        }

        public Criteria andTerminalVehiclesIdLessThan(Integer value) {
            addCriterion("terminal_vehicles_id <", value, "terminalVehiclesId");
            return (Criteria) this;
        }

        public Criteria andTerminalVehiclesIdLessThanOrEqualTo(Integer value) {
            addCriterion("terminal_vehicles_id <=", value, "terminalVehiclesId");
            return (Criteria) this;
        }

        public Criteria andTerminalVehiclesIdIn(List<Integer> values) {
            addCriterion("terminal_vehicles_id in", values, "terminalVehiclesId");
            return (Criteria) this;
        }

        public Criteria andTerminalVehiclesIdNotIn(List<Integer> values) {
            addCriterion("terminal_vehicles_id not in", values, "terminalVehiclesId");
            return (Criteria) this;
        }

        public Criteria andTerminalVehiclesIdBetween(Integer value1, Integer value2) {
            addCriterion("terminal_vehicles_id between", value1, value2, "terminalVehiclesId");
            return (Criteria) this;
        }

        public Criteria andTerminalVehiclesIdNotBetween(Integer value1, Integer value2) {
            addCriterion("terminal_vehicles_id not between", value1, value2, "terminalVehiclesId");
            return (Criteria) this;
        }

        public Criteria andTerminalIdIsNull() {
            addCriterion("terminal_id is null");
            return (Criteria) this;
        }

        public Criteria andTerminalIdIsNotNull() {
            addCriterion("terminal_id is not null");
            return (Criteria) this;
        }

        public Criteria andTerminalIdEqualTo(Integer value) {
            addCriterion("terminal_id =", value, "terminalId");
            return (Criteria) this;
        }

        public Criteria andTerminalIdNotEqualTo(Integer value) {
            addCriterion("terminal_id <>", value, "terminalId");
            return (Criteria) this;
        }

        public Criteria andTerminalIdGreaterThan(Integer value) {
            addCriterion("terminal_id >", value, "terminalId");
            return (Criteria) this;
        }

        public Criteria andTerminalIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("terminal_id >=", value, "terminalId");
            return (Criteria) this;
        }

        public Criteria andTerminalIdLessThan(Integer value) {
            addCriterion("terminal_id <", value, "terminalId");
            return (Criteria) this;
        }

        public Criteria andTerminalIdLessThanOrEqualTo(Integer value) {
            addCriterion("terminal_id <=", value, "terminalId");
            return (Criteria) this;
        }

        public Criteria andTerminalIdIn(List<Integer> values) {
            addCriterion("terminal_id in", values, "terminalId");
            return (Criteria) this;
        }

        public Criteria andTerminalIdNotIn(List<Integer> values) {
            addCriterion("terminal_id not in", values, "terminalId");
            return (Criteria) this;
        }

        public Criteria andTerminalIdBetween(Integer value1, Integer value2) {
            addCriterion("terminal_id between", value1, value2, "terminalId");
            return (Criteria) this;
        }

        public Criteria andTerminalIdNotBetween(Integer value1, Integer value2) {
            addCriterion("terminal_id not between", value1, value2, "terminalId");
            return (Criteria) this;
        }

        public Criteria andVehicleIdIsNull() {
            addCriterion("vehicle_id is null");
            return (Criteria) this;
        }

        public Criteria andVehicleIdIsNotNull() {
            addCriterion("vehicle_id is not null");
            return (Criteria) this;
        }

        public Criteria andVehicleIdEqualTo(Integer value) {
            addCriterion("vehicle_id =", value, "vehicleId");
            return (Criteria) this;
        }

        public Criteria andVehicleIdNotEqualTo(Integer value) {
            addCriterion("vehicle_id <>", value, "vehicleId");
            return (Criteria) this;
        }

        public Criteria andVehicleIdGreaterThan(Integer value) {
            addCriterion("vehicle_id >", value, "vehicleId");
            return (Criteria) this;
        }

        public Criteria andVehicleIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("vehicle_id >=", value, "vehicleId");
            return (Criteria) this;
        }

        public Criteria andVehicleIdLessThan(Integer value) {
            addCriterion("vehicle_id <", value, "vehicleId");
            return (Criteria) this;
        }

        public Criteria andVehicleIdLessThanOrEqualTo(Integer value) {
            addCriterion("vehicle_id <=", value, "vehicleId");
            return (Criteria) this;
        }

        public Criteria andVehicleIdIn(List<Integer> values) {
            addCriterion("vehicle_id in", values, "vehicleId");
            return (Criteria) this;
        }

        public Criteria andVehicleIdNotIn(List<Integer> values) {
            addCriterion("vehicle_id not in", values, "vehicleId");
            return (Criteria) this;
        }

        public Criteria andVehicleIdBetween(Integer value1, Integer value2) {
            addCriterion("vehicle_id between", value1, value2, "vehicleId");
            return (Criteria) this;
        }

        public Criteria andVehicleIdNotBetween(Integer value1, Integer value2) {
            addCriterion("vehicle_id not between", value1, value2, "vehicleId");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}